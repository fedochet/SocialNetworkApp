package listeners;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import dao.h2.H2PostDAO;
import dao.h2.H2UserDAO;
import dao.interfaces.PostDAO;
import dao.interfaces.UserDAO;
import services.SecurityService;
import utils.SQLUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by roman on 14.07.2016.
 */

@WebListener
public class ServicesProvider implements ServletContextListener {

    private static final String RESOURCES_FILE_PATH = "/WEB-INF/resources/";
    private static final String DB_PROPERTIES_FILE_NAME = "db.properties";
    private static final String DB_PREPARE_FILE_NAME = "001_initial_db_structure.sql";

    public static final String USER_DAO = UserDAO.class.getName();
    public static final String POST_DAO = PostDAO.class.getName();
    public static final String SECURITY_SERVICE = SecurityService.class.getName();

    private ConnectionPool connectionPool;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String propertiesFilePath = servletContext.getRealPath(RESOURCES_FILE_PATH + DB_PROPERTIES_FILE_NAME);

        connectionPool = SimpleConnectionPool.create(propertiesFilePath);
        UserDAO userDAO = new H2UserDAO(connectionPool);
        PostDAO postDAO = new H2PostDAO(connectionPool);
        SecurityService securityService = new SecurityService();

        servletContext.setAttribute(USER_DAO, userDAO);
        servletContext.setAttribute(POST_DAO, postDAO);
        servletContext.setAttribute(SECURITY_SERVICE, securityService);

        String scriptFilePath = servletContext.getRealPath(RESOURCES_FILE_PATH + DB_PREPARE_FILE_NAME);
        SQLUtils.executeScript(connectionPool, scriptFilePath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            connectionPool.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
