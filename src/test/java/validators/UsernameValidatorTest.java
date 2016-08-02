package validators;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by roman on 18.07.2016.
 */
public class UsernameValidatorTest {

    @Test
    public void testValidUsernames() {
        assertTrue(UsernameValidator.validate("test"));
        assertTrue(UsernameValidator.validate("anstreth"));
        assertTrue(UsernameValidator.validate("12kanye--41west"));
        assertTrue(UsernameValidator.validate("_FuCkThEpOlIcE_"));
    }

    @Test
    public void shorterThen3SymbolsNotAllowed() {
        assertFalse(UsernameValidator.validate(""));
        assertFalse(UsernameValidator.validate("_-"));
        assertFalse(UsernameValidator.validate("12"));
        assertFalse(UsernameValidator.validate("B"));
        assertFalse(UsernameValidator.validate("ff"));
    }

    @Test
    public void longerThan15SymbolsNotAllowed() {
        assertFalse(UsernameValidator.validate("12345678901234567890"));
        assertFalse(UsernameValidator.validate("abcdefghijklmnop"));
        assertFalse(UsernameValidator.validate("_________---_-_-"));
    }

    @Test
    public void cyrillicUsernamesAreNotAllowed() {
        assertFalse(UsernameValidator.validate("тесттест"));
        assertFalse(UsernameValidator.validate("Меня зовут джон"));
    }

    @Test
    public void specialCharactersAreNotAllowed() {
        assertFalse(UsernameValidator.validate("myNameIs$krill"));
        assertFalse(UsernameValidator.validate("It'sMyParty"));
        assertFalse(UsernameValidator.validate("(|___|)"));
        assertFalse(UsernameValidator.validate("//itsforUTF8!"));
        assertFalse(UsernameValidator.validate("my@mail.com"));
    }

    @Test
    public void nullabilityCheck() {
        assertFalse(UsernameValidator.validate(null));
    }

}