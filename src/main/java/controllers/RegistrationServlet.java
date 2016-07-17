package controllers;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import model.User;
import services.SecurityService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static utils.GeneralUtils.mapOrNull;
import static utils.HttpServletUtils.removeSessionAttributes;

/**
 * Created by roman on 14.07.2016.
 */

@WebServlet(urlPatterns = "/registration")
public class RegistrationServlet extends HttpServlet {

    private UserDAO userDAO;
    private SecurityService securityService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        userDAO = (UserDAO) context.getAttribute(ServicesProvider.USER_DAO);
        securityService = (SecurityService) context.getAttribute(ServicesProvider.SECURITY_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("registration.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = req.getParameter("j_username");
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        LocalDate birthDate;
        try {
            birthDate = mapOrNull(req.getParameter("birth_date"), LocalDate::parse);
        } catch (DateTimeParseException e) {
            birthDate = null;
        }
        String password = req.getParameter("j_password");
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(securityService.encryptPassword(password));
        user.setBirthDate(birthDate);

        if (!userDAO.getByUsername(username).isPresent()) {
            try {
                userDAO.create(user);
                user = userDAO.getByUsername(username).orElseThrow(RuntimeException::new);

                removeSessionAttributes(session);
                session.setAttribute("user", user);
                resp.sendRedirect("");
            } catch (RuntimeException e) {
                refuseRegistration(req, resp, user, "Some error occured; user not created.");
            }
        } else {
            refuseRegistration(req, resp, user, "This username is already used!");
        }
    }

    private void refuseRegistration(HttpServletRequest request, HttpServletResponse response, User user, String reason) throws IOException {
        HttpSession session = request.getSession();
        removeSessionAttributes(session);
        session.setAttribute("error_message", reason);
        session.setAttribute("j_username", user.getUsername());
        session.setAttribute("first_name", user.getFirstName());
        session.setAttribute("last_name", user.getLastName());
        session.setAttribute("birth_date", mapOrNull(user.getBirthDate(), LocalDate::toString));
        response.sendRedirect("registration");
    }
}
