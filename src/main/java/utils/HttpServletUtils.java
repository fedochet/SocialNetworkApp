package utils;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Created by roman on 16.07.2016.
 */
public interface HttpServletUtils {
    static void removeSessionAttributes(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.removeAttribute(attributeNames.nextElement());
        }
    }
}
