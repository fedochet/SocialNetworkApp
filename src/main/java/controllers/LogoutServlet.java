package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roman on 19.07.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET on {} path", req.getServletPath());

        User sessionUser = (User) req.getSession().getAttribute("sessionUser");
        if (sessionUser!=null) {
            log.info("User with '{}' username logged out; redirecting to landing page", sessionUser.getUsername());
        } else {
            log.info("No user were attached to session; redirection to landing page");
        }

        req.getSession().removeAttribute("sessionUser");
        resp.sendRedirect("/");
    }
}
