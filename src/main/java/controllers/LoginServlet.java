package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 16.07.2016.
 */
@Slf4j
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST request on {} path.", req.getServletPath());

        HttpSession session = req.getSession();

        String username = req.getParameter("j_username");
        String password = req.getParameter("j_password");
        String nextURL = Optional.ofNullable((String) session.getAttribute("next")).orElse("/");

        Optional<User> userOpt = userDAO.getByUsername(username);

        if (!userOpt.isPresent()) {
            log.warn("User passed not existing username '{}'! Redirecting to error page", username);
            resp.sendError(406, "Wrong username");
            return;
        }

        User user = userOpt.get();
        if (securityService.validatePassword(password, user.getPassword())) {
            log.info("Successful login as '{}'; redirecting to {}", user.getUsername(), nextURL);
            session.setAttribute("sessionUser", user);
            resp.sendRedirect(nextURL);
        } else {
            log.warn("User with '{}' username passed invalid password! Redirecting to error page", user.getUsername());
            resp.sendError(406, "Wrong password");
        }
    }
}
