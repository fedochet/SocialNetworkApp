package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;
import model.UserRole;
import validators.UsernameValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 14.07.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/registration")
public class RegistrationServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {} path", req.getServletPath());

        HttpSession session = req.getSession();

        String username = req.getParameter("j_username");
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        LocalDate birthDate;
        try {
            birthDate = mapOrNull(req.getParameter("birth_date"), LocalDate::parse);
        } catch (DateTimeParseException e) {
            log.warn("Birth date parse error! Setting to null", e);
            birthDate = null;
        }
        String password = req.getParameter("j_password");

        if (!UsernameValidator.validate(username)) {
            log.info("'{}', passed as username, is invalid! Redirecting to error page.", username);
            resp.sendError(406, "This username is not valid");
            return;
        }

        if (userDAO.getByUsername(username).isPresent()) {
            log.warn("'{}', passed as username, is already used! Redirecting to error page.", username);
            resp.sendError(406, "This username is already used");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(securityService.encryptPassword(password));
        user.setBirthDate(birthDate);
        user.setRole(UserRole.USER);
        user.setInfo("This is my personal page!");

        log.info("Trying to safe this user: {}", user);

        String nextURL = Optional.ofNullable((String) session.getAttribute("next")).orElse("/");

        try {
            userDAO.create(user);
            user = userDAO.getByUsername(username).orElseThrow(RuntimeException::new);

            session.setAttribute("sessionUser", user);

            log.info("User '{}' created and added to current session. Redirecting to {}", user, nextURL);
            resp.sendRedirect(nextURL);
        } catch (RuntimeException e) {
            log.warn("Cannot save user: " + user.toString()+", redirecting to error page", e);
            resp.sendError(500, "Error while saving user; try again");
        }
    }
}
