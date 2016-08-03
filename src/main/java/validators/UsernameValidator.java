package validators;

import java.util.regex.Pattern;

/**
 * Created by roman on 18.07.2016.
 */
public final class UsernameValidator {

    private UsernameValidator() {}

    // letters, digits, _, -, from 3 to 15 length
    private static final String usernamePattern = "^[0-9A-Za-z-_]{3,15}$";
    private static final Pattern pattern = Pattern.compile(usernamePattern);


    /**
     * Checks if passed username is valid username.
     * @param username if null - returns false
     * @return checks status
     */
    public static boolean validate(String username) {
        return username != null && pattern.matcher(username).matches();

    }
}
