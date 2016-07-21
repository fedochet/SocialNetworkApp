package controllers;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import model.User;
import services.SecurityService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 16.07.2016.
 */
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private SecurityService securityService;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        securityService = (SecurityService) getServletContext().getAttribute(ServicesProvider.SECURITY_SERVICE);
        userDAO = (UserDAO) getServletContext().getAttribute(ServicesProvider.USER_DAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = req.getParameter("j_username");
        String password = req.getParameter("j_password");
        String nextURL = Optional.ofNullable((String) session.getAttribute("next")).orElse("/");

        Optional<User> userOpt = userDAO.getByUsername(username);

        if (!userOpt.isPresent()) {
            resp.sendError(406, "Wrong username");
            return;
        }

        User user = userOpt.get();
        if (securityService.validatePassword(password, user.getPassword())) {
            session.setAttribute("user", user);
            resp.sendRedirect(nextURL);
        } else {
            resp.sendError(406, "Wrong password");
        }
    }
}
