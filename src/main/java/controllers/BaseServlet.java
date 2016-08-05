package controllers;

import dao.interfaces.PostDAO;
import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import services.SecurityService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by roman on 05.08.2016.
 */
public class BaseServlet extends HttpServlet {
    protected UserDAO userDAO;
    protected PostDAO postDAO;
    protected SecurityService securityService;

    @Override
    public void init() throws ServletException {
        userDAO = (UserDAO) getServletContext().getAttribute(ServicesProvider.USER_DAO);
        postDAO = (PostDAO) getServletContext().getAttribute(ServicesProvider.POST_DAO);
        securityService = (SecurityService) getServletContext().getAttribute(ServicesProvider.SECURITY_SERVICE);
    }
}
