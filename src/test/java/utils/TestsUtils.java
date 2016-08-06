package utils;

import model.User;
import model.UserRole;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by roman on 06.08.2016.
 */
public class TestsUtils {
    public static User getTestUser() {
        User testUser = new User();
        testUser.setUsername("TestUser");
        testUser.setFirstName("roman");
        testUser.setLastName("golyshev");
        testUser.setPassword("test");
        testUser.setInfo("My status");
        testUser.setBirthDate(LocalDate.now());
        testUser.setRegistrationTime(Instant.now());
        testUser.setRole(UserRole.ADMIN);

        return testUser;
    }

    public static User getFollower() {
        User follower = new User();
        follower.setUsername("follower");
        follower.setPassword("1111");
        follower.setRole(UserRole.USER);

        return follower;
    }

}
