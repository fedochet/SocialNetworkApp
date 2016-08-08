package controllers;

import dao.interfaces.FollowerDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;
import validators.UsernameValidator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 21.07.2016.
 */
@Slf4j
@WebServlet(urlPatterns = "/user/*")
public class UserPageServlet extends BaseServlet {

    private RequestDispatcher userPageJSPDisspatcher;
    private FollowerDAO followerDAO;

    private static String removeLeadingSlash(String path) {
        return path.substring(1);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        followerDAO = (FollowerDAO) getServletContext().getAttribute(ServicesProvider.FOLLOWER_DAO);
        userPageJSPDisspatcher = getServletContext().getRequestDispatcher("/WEB-INF/user_page.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving GET on {} path", req.getServletPath() + req.getPathInfo());

        Optional<User> userOpt
                = Optional.ofNullable(req.getPathInfo())
                    .map(UserPageServlet::removeLeadingSlash)
                    .filter(UsernameValidator::validate)
                    .flatMap(userDAO::getByUsername);

        if (!userOpt.isPresent()) {
            log.warn("User '{}' does not exist! Redirecting to error page.", req.getPathInfo());

            resp.sendError(406, "This user doesn't exist!");
            return;
        }

        log.info("User '{}' exists; forwarding to user_page.jsp", userOpt.get().getUsername());
        req.setAttribute("pageUser", userOpt.get());

        boolean canFollow
                = SessionUtils.getSessionUserOpt(req.getSession())
                        .map(User::getId)
                        .map(id -> !followerDAO.isFollowing(userOpt.get().getId(), id))
                        .orElse(true);

        req.setAttribute("canFollow", canFollow);

        userPageJSPDisspatcher.forward(req, resp);
    }
}
