package controllers;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import validators.UsernameValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 05.08.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/changeuser")
public class ChangeUserServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = (UserDAO) getServletContext().getAttribute(ServicesProvider.USER_DAO);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {} path", req.getServletPath());

        User sessionUser = (User) req.getSession().getAttribute("sessionUser");

        String newUsername = req.getParameter("j_username");

        if (!UsernameValidator.validate(newUsername)) {
            log.info("'{}', passed as username, is invalid! Redirecting to error page.", newUsername);
            resp.sendError(406, "This username is not valid");
            return;
        }

        if (userDAO.getByUsername(newUsername).isPresent()) {
            log.warn("'{}', passed as username, is already used! Redirecting to error page.", newUsername);
            resp.sendError(406, "This username is already used");
            return;
        }

        String newFirstName = req.getParameter("first_name");
        String newLastName = req.getParameter("last_name");

        String newInfo = req.getParameter("info");

        LocalDate newBirthDate;
        try {
            newBirthDate = mapOrNull(req.getParameter("birth_date"), LocalDate::parse);
        } catch (DateTimeParseException e) {
            log.warn("Birth date parse error! Setting to previosly setted", e);
            newBirthDate = sessionUser.getBirthDate();
        }

        try {
            sessionUser.setUsername(newUsername);
            sessionUser.setFirstName(newFirstName);
            sessionUser.setLastName(newLastName);
            sessionUser.setInfo(newInfo);
            sessionUser.setBirthDate(newBirthDate);

            log.info("Trying to update user: '{}'", sessionUser);

            if (userDAO.update(sessionUser)) {
                log.info("User is successfully updated! Forwarding to /");
                req.getRequestDispatcher("/").forward(req, resp);
            } else {
                log.warn("Update went unsuccessful! Updating didn't return 'true'. Redirecting to error page");
                resp.sendError(500, "Error while updating user; try again");
            }
        } catch (RuntimeException e) {
            log.warn("Updating user '" + sessionUser + "' was unsuccessful", e);
            resp.sendError(500, "Error while updating user; try again");
        }
    }
}
