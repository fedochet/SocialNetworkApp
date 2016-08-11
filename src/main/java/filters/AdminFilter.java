package filters;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import model.UserRole;
import utils.SessionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by roman on 11.08.2016.
 */
@Slf4j
@WebFilter(urlPatterns = {"/admin/*", "/rest/admin/*"})
public class AdminFilter implements Filter {

    private static UserDAO userDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userDAO = (UserDAO) filterConfig.getServletContext().getAttribute(ServicesProvider.USER_DAO);
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Filtering on {} path", request.getServletPath() + request.getPathInfo());

        Optional<User> adminUser = SessionUtils.getSessionUserOpt(request.getSession(false))
                .map(User::getId)
                .flatMap(userDAO::getById)
                .filter(u -> u.getRole().equals(UserRole.ADMIN));

        if (!adminUser.isPresent()) {
            log.warn("This user is not admin; forwarding to /");
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }

        log.warn("This user is admin; processing request");
        chain.doFilter(request, response);
    }

        @Override
    public void destroy() {

    }
}
