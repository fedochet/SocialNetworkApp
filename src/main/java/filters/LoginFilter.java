package filters;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by roman on 17.07.2016.
 */

@Slf4j
@WebFilter(urlPatterns = {
        "/home",
        "/addpost",
        "/settings",
        "/secure/*",
        "/rest/secure/*",
        "/admin/*",
        "/rest/admin/*"})
public class LoginFilter implements Filter {
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
        log.info("Filtering {} path", request.getServletPath() + request.getPathInfo());

        Optional<User> userOpt
                = SessionUtils.getSessionUserOpt(request.getSession(false))
                .flatMap(this::checkAndUpdateUser);

        if (userOpt.isPresent()) {
            log.info("Logged as '{}'", userOpt.get().getUsername());
            SessionUtils.setSessionUser(request.getSession(), userOpt.get());
            chain.doFilter(request, response);
            return;
        }

        log.info("No validated user is attached to session; redirecting to landing page");
        SessionUtils.clearSessionUser(request.getSession(false));
        request.getRequestDispatcher("/").forward(request, response);
    }

    private Optional<User> checkAndUpdateUser(User user) {
        return userDAO.getById(user.getId())
                .filter(dbUser -> Objects.equals(dbUser.getPassword(), user.getPassword()));
    }


    @Override
    public void destroy() {

    }
}
