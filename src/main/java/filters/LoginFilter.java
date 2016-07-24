package filters;

import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
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

@WebFilter("/*")
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
        String requestURI = request.getRequestURI();
        if (Objects.equals(requestURI, "/login") || Objects.equals(requestURI, "/registration")) {
            chain.doFilter(request, response);
            return;
        }

        Optional<User> userOpt
                = Optional.ofNullable(request.getSession())
                .flatMap(this::getUser)
                .map(User::getId)
                .flatMap(userDAO::getById);

        if (userOpt.isPresent()) {
            request.getSession().setAttribute("sessionUser", userOpt.get());
            chain.doFilter(request, response);
            return;
        }

        request.getSession().removeAttribute("sessionUser");
        request.getRequestDispatcher("/login").forward(request, response);
    }


    @Override
    public void destroy() {

    }
}
