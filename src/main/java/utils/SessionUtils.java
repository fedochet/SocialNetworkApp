package utils;

import model.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Created by roman on 07.08.2016.
 */
public class SessionUtils {

    private static final String SESSION_USER_KEY = "sessionUser";

    public static Optional<User> getSessionUser(HttpSession session) {
        return Optional.ofNullable(session)
                .map(s -> (User)s.getAttribute(SESSION_USER_KEY));
    }

    public static void setSessionUser(HttpSession session, User sessionUser) {
        session.setAttribute(SESSION_USER_KEY,sessionUser);
    }

    public static void clearSessionUser(HttpSession session) {
        if (session!=null) session.removeAttribute(SESSION_USER_KEY);
    }
}
