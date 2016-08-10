package controllers;

import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
public class IndexServlet extends BaseServlet{

    @Override
    public void init() throws ServletException {
        super.init();
        homePageJSPDispatcher = getServletContext().getRequestDispatcher("/WEB-INF/home_page.jsp");
        landingPageJSPDispatcher = getServletContext().getRequestDispatcher("/WEB-INF/landing_page.jsp");
    }

    private RequestDispatcher homePageJSPDispatcher;
    private RequestDispatcher landingPageJSPDispatcher;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET request on {} path.", "/");

        Optional<User> sessionUserOpt
                = SessionUtils.getSessionUserOpt(req.getSession(false))
                .map(User::getId)
                .flatMap(userDAO::getById);

        if (sessionUserOpt.isPresent()) {
            log.info("Logged in as '{}'; forwarding to home_page", sessionUserOpt.get().getUsername());
            req.setAttribute("subscribesNumber", followerDAO.getNumberOfSubscribes(sessionUserOpt.get().getId()));
            req.setAttribute("followersNumber", followerDAO.getNumberOfFollowers(sessionUserOpt.get().getId()));
            homePageJSPDispatcher.forward(req, resp);
        } else {
            log.info("User session is not attached; forwarding to landing_page.");
            landingPageJSPDispatcher.forward(req, resp);
        }

    }

    private static Optional<User> getUser(HttpSession session) {
        return Optional.ofNullable((User)session.getAttribute("sessionUser"));
    }
}
