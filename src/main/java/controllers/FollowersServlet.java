package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;

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
@WebServlet(urlPatterns = "/followers/*")
public class FollowersServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET on {} path", req.getServletPath() + req.getPathInfo());

        Optional<User> pageUser = Optional.ofNullable(req.getPathInfo())
                .filter(s->s.length()!=1)
                .map(s -> s.substring(1))
                .flatMap(userDAO::getByUsername);

        if (!pageUser.isPresent()) {
            resp.sendError(404, "No such user exists!");
            return;
        }

        req.setAttribute("pageUser", pageUser.get());
        req.setAttribute("usersList",
                followerDAO.getAllFollowers(pageUser.get().getId()));
        req.getRequestDispatcher("/WEB-INF/followers_page.jsp").forward(req, resp);
    }
}
