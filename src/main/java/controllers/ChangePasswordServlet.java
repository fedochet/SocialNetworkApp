package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by roman on 05.08.2016.
 */
@Slf4j
@WebServlet(urlPatterns = "/secure/changepassword")
public class ChangePasswordServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {} path", req.getServletPath());

        User sessionUser = (User) req.getSession().getAttribute("sessionUser");
        log.info("user '{}' is trying to change password", sessionUser.getUsername());

        String oldPassword = req.getParameter("old_password");

        if (!securityService.validatePassword(oldPassword, sessionUser.getPassword())) {
            log.warn("Old password is incorrect; redirecting to error page");
            resp.sendError(406, "Old password is incorrect");
            return;
        }

        log.info("Old password is correct; checking new password");

        String newPassword = req.getParameter("new_password");
        String newPasswordTest = req.getParameter("new_password_test");

        if (newPassword == null || newPassword.isEmpty() || !Objects.equals(newPassword, newPasswordTest)) {
            log.info("new password is null, wrong or not equals to test password; redirecting to error page");
            resp.sendError(406, "New password is incorrect");
            return;
        }

        try {
            sessionUser.setPassword(securityService.encryptPassword(newPassword));
            log.info("New password is ok; trying to update user '{}'", sessionUser);
            if (userDAO.update(sessionUser)) {
                log.info("User password is successfully updated! Redirecting to /");
                resp.sendRedirect("/");
            } else {
                log.warn("Password update went unsuccessful! Updating didn't return 'true'. Redirecting to error page");
                resp.sendError(500, "Error while updating password; try again");
            }
        } catch (RuntimeException e) {
            log.warn("Updating user '" + sessionUser + "' password was unsuccessful", e);
            resp.sendError(500, "Error while updating user; try again");
        }
    }
}
