package controllers.admin;

import controllers.BaseServlet;
import lombok.extern.slf4j.Slf4j;
import model.User;
import model.UserRole;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 11.08.2016.
 */
@Slf4j
@WebServlet(urlPatterns = "/admin/changerole")
public class AdminChangeUserRoleServlet extends BaseServlet {

    private int getIntFromRequest(HttpServletRequest request, String paramName, int defaultValue) {
        try {
            return Optional.ofNullable(request.getParameter(paramName))
                    .map(Integer::parseInt)
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            log.warn("Number is in wrong format, cannot parse", e);
            return defaultValue;
        }
    }

    private int getUserId(HttpServletRequest request) {
        return getIntFromRequest(request, "user_id", -1);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {}", req.getServletPath());

        int userId = getUserId(req);
        if (userId == -1) {
            log.info("No userId parameter! Redirecting to /admin/adminpage");
            resp.sendRedirect("/admin/adminpage");
            return;
        }

        Optional<User> userToChangeOpt =
                userDAO.getById(userId);

        if (!userToChangeOpt.isPresent()) {
            log.warn("This user does not exists! Redirecting to error page");
            resp.sendError(404, "This user does  not exist");
            return;
        }

        User userToChange = userToChangeOpt.get();

        boolean isSameUser = SessionUtils.getSessionUser(req.getSession()).getId()==userToChange.getId();

        if (isSameUser) {
            log.warn("Admin is Trying to change itself role! Redirecting to error page");
            resp.sendError(406, "You can't change your own role");
            return;
        }


        if (userToChange.getRole().equals(UserRole.ADMIN)) {
            userToChange.setRole(UserRole.USER);
        } else {
            userToChange.setRole(UserRole.ADMIN);
        }

        try {
            log.info("Trying to set user {} role to {}", userToChange.getUsername(), userToChange.getRole());
            if (userDAO.update(userToChange)){
                log.info("Updated user {} role to {} successfully! Redirecting to /admin/adminpage", userToChange.getUsername(), userToChange.getRole());
                resp.sendRedirect("/admin/adminpage");
            } else {
                log.warn("Update went unsuccessfull; no such user found. Redirecting to error page");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Update went unsuccessfull; no such user found");
            }
        } catch (RuntimeException e) {
            log.warn("Error occured while trying to update user. Redirect to error page",e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occured while trying to delete user");
        }
    }
}
