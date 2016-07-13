package services;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by roman on 13.07.2016.
 */
public class SecurityServiceTest {
    SecurityService service = new SecurityService();

    @Test
    public void passwordEncryptedAndCanBeDecrypted() {
        String password = "qwerty";
        String hash = service.encryptPassword(password);

        assertNotNull(hash);
        assertThat(hash, is(not(password)));

        assertTrue(service.validatePassword(password, hash));
        assertFalse(service.validatePassword(password, "wrong hash"));
        assertFalse(service.validatePassword("wrong password", hash));
    }

    @Test
    public void samePasswordsHaveDifferentHashes() {
        String password = "qwerty123";
        String hash1 = service.encryptPassword(password);
        String hash2 = service.encryptPassword(password);
        String hash3 = service.encryptPassword(password);

        assertThat(hash1, is(not(hash2)));
        assertThat(hash2, is(not(hash3)));

        assertTrue(service.validatePassword(password, hash1));
        assertTrue(service.validatePassword(password, hash2));
        assertTrue(service.validatePassword(password, hash3));
    }

    @Test
    public void longPassword() {
        String longPassword = "123456789testesatsdoaasdklkqwLKANLAKDNLknqldknlsidhiahslkznsldkasldjwlekjalkjdlkajsldkjzlk";

        String hash = service.encryptPassword(longPassword);
        assertTrue(service.validatePassword(longPassword, hash));
        assertTrue(hash.length()<=255);
    }
}