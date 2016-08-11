package controllers.admin;

import controllers.BaseServlet;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 11.08.2016.
 */
@WebServlet("/admin/removeuser")
@Slf4j
public class AdminRemoveUserServlet extends BaseServlet {

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

        log.info("Trying to delete user with id {}", userId);
        try {
            if (userDAO.deleteById(userId)){
                log.info("User is successfully deleted!");
            } else {
                log.warn("Something went wrong; user is not deleted");
            }

            log.info("Redirecting to /admin/adminpage");

            resp.sendRedirect("/admin/adminpage");
        } catch (RuntimeException e) {
            log.warn("Error occured while trying to delete user. Redirect to error page",e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occured while trying to delete user");
        }
    }
}
