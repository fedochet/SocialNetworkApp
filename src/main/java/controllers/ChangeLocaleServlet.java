package controllers;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 10.08.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/changelocale")
public class ChangeLocaleServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {} path", req.getServletPath());

        String nextUrl = Optional.ofNullable(req.getParameter("next_url")).orElse("/");

        Optional.ofNullable(req.getParameter("locale"))
                .ifPresent(locale -> {
                    log.info("Setting locale to {}", locale);
                    req.getSession().setAttribute("locale", locale);
                });

        log.info("Redirecting to {}", nextUrl);
        resp.sendRedirect(nextUrl);
    }
}
