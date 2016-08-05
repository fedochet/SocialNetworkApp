package filters;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by roman on 17.07.2016.
 */

@Slf4j
@WebFilter(urlPatterns = {"/home", "/addpost", "/settings"})
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

    private Optional<User> getUser(HttpSession session) {
        return Optional.ofNullable((User) session.getAttribute("sessionUser"));
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Filtering {} path", request.getServletPath());

        Optional<User> userOpt
                = Optional.ofNullable(request.getSession())
                .flatMap(this::getUser)
                .flatMap(this::checkAndUpdateUser);

        if (userOpt.isPresent()) {
            log.info("Logged as '{}'", userOpt.get().getUsername());
            request.getSession().setAttribute("sessionUser", userOpt.get());
            chain.doFilter(request, response);
            return;
        }

        log.info("No validated user is attached to session; redirecting to landing page");
        request.getSession().removeAttribute("sessionUser");
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
