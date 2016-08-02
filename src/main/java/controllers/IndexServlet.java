package controllers;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 29.07.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "")
public class IndexServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = (UserDAO) getServletContext().getAttribute(ServicesProvider.USER_DAO);
        homeDispatcher = getServletContext().getRequestDispatcher("/home");
        landingDispatcher = getServletContext().getRequestDispatcher("/landing");
    }

    private RequestDispatcher homeDispatcher;
    private RequestDispatcher landingDispatcher;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET request on {} path.", "/");

        Optional<User> userOpt
                = Optional.ofNullable(req.getSession())
                .flatMap(IndexServlet::getUser)
                .map(User::getId)
                .flatMap(userDAO::getById);

        if (userOpt.isPresent()) {
            log.info("Logged in as '{}'; redirecting to home_page", userOpt.get().getUsername());
            homeDispatcher.forward(req, resp);
        } else {
            log.info("User session is not attached; redirecting to landing_page.");
            landingDispatcher.forward(req, resp);
        }

    }

    private static Optional<User> getUser(HttpSession session) {
        return Optional.ofNullable((User)session.getAttribute("sessionUser"));
    }
}
