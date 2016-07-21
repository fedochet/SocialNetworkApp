package controllers;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import model.User;
import validators.UsernameValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 21.07.2016.
 */
@WebServlet(urlPatterns = "/user/*")
public class UserPageServlet extends HttpServlet {
    private UserDAO userDAO;

    private static String removeLeadingSlash(String path) {
        return path.substring(1);
    }

    @Override
    public void init() throws ServletException {
        userDAO = (UserDAO) getServletContext().getAttribute(ServicesProvider.USER_DAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<User> userOpt
                = Optional.ofNullable(req.getPathInfo())
                    .map(UserPageServlet::removeLeadingSlash)
                    .filter(UsernameValidator::validate)
                    .flatMap(userDAO::getByUsername);

        if (!userOpt.isPresent()) {
            resp.sendError(406, "This user doesn't exist!");
            return;
        }

        req.setAttribute("pageUser", userOpt.get());
        req.getRequestDispatcher("/user_page.jsp").forward(req, resp);
    }
}
