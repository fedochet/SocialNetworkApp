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
@Slf4j
@WebServlet(urlPatterns = "/admin/adminpage")
public class AdminPageServlet extends BaseServlet {

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

    private int getOffsetId(HttpServletRequest request) {
        return getIntFromRequest(request, "offset_id", -1);
    }

    private int getLimit(HttpServletRequest request) {
        return getIntFromRequest(request, "limit", 5);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET on {} path", req.getServletPath());

        int offsetId = getOffsetId(req);
        int limit = getLimit(req);

        req.setAttribute("users", userDAO.getUsers(offsetId, limit));
        req.setAttribute("offsetId", offsetId);
        req.setAttribute("limit", limit);

        req.getRequestDispatcher("/WEB-INF/admin_page.jsp").forward(req, resp);
    }
}
