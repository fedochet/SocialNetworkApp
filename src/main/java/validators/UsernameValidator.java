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


    public static boolean validate(String username) {
        return pattern.matcher(username).matches();
    }
}
