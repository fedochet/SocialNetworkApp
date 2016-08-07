package utils;

import dao.interfaces.PostDAO;
import model.Post;
import model.User;
import model.UserRole;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by roman on 06.08.2016.
 */
public interface TestsUtils {
    static User getTestUser() {
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

    static User getFollower() {
        User follower = new User();
        follower.setUsername("follower");
        follower.setPassword("1111");
        follower.setRole(UserRole.USER);

        return follower;
    }

    static Set<Integer> add100Posts(User testUser, PostDAO postDAO) {
        Set<Integer> newPosts = new HashSet<>();

        for (int i = 0; i<100; i++) {
            Post post = new Post();
            post.setAuthorId(testUser.getId());

            newPosts.add(postDAO.create(post));
        }

        return newPosts;
    }

    static void deleteAllPostsById(Set<Integer> postIds, PostDAO postDAO) {
        postIds.forEach(postDAO::deleteById);
    }


}
