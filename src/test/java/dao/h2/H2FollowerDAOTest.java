package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.SQLUtils;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static utils.TestsUtils.getFollower;
import static utils.TestsUtils.getTestUser;

/**
 * Created by roman on 06.08.2016.
 */
public class H2FollowerDAOTest {

    private static ConnectionPool connectionPool;
    private static H2UserDAO userDAO;
    private static H2FollowerDAO followerDAO;
    private static String RESOURCES_DIR = "src/test/resources/";
    private static String DB_SCRIPT = "001_initial_db_structure.sql";
    private static String DB_PROPERTIES = "db.properties";

    @BeforeClass
    public static void beforeClass() {
        connectionPool = SimpleConnectionPool.create(RESOURCES_DIR + DB_PROPERTIES);
        userDAO = new H2UserDAO(connectionPool);
        followerDAO = new H2FollowerDAO(connectionPool);
        SQLUtils.executeScript(connectionPool, RESOURCES_DIR + DB_SCRIPT);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connectionPool.close();
    }

    @Test
    public void afterAddFollowerUserBecomesFollower() {
        User testUser = getTestUser();
        testUser.setId(userDAO.create(testUser));

        User follower = getFollower();
        follower.setId(userDAO.create(follower));

        assertFalse(followerDAO.isFollowing(testUser.getId(), follower.getId()));
        assertFalse(followerDAO.removeFollower(testUser.getId(), follower.getId()));

        assertTrue(followerDAO.addFollower(testUser.getId(), follower.getId()));
        assertTrue(followerDAO.isFollowing(testUser.getId(), follower.getId()));
        assertFalse(followerDAO.addFollower(testUser.getId(), follower.getId()));

        assertTrue(followerDAO.removeFollower(testUser.getId(), follower.getId()));
        assertFalse(followerDAO.isFollowing(testUser.getId(), follower.getId()));
        assertFalse(followerDAO.removeFollower(testUser.getId(), follower.getId()));

        userDAO.deleteById(testUser.getId());
        userDAO.deleteById(follower.getId());
    }

    @Test
    public void youCanGetAllFollowersAndSubscriptions() {
        User testUser = getTestUser();
        testUser.setId(userDAO.create(testUser));

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("user1");
        user1.setId(userDAO.create(user1));

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("user2");
        user2.setId(userDAO.create(user2));

        assertTrue(followerDAO.getAllFollowers(testUser.getId()).isEmpty());
        assertTrue(followerDAO.getAllSubscriptions(testUser.getId()).isEmpty());

        followerDAO.addFollower(testUser.getId(), user1.getId());
        assertThat(followerDAO.getAllFollowers(testUser.getId()).size(), is(1));
        assertThat(followerDAO.getAllFollowers(testUser.getId()).get(0),
                is(userDAO.getById(user1.getId()).get()));
        assertTrue(followerDAO.getAllSubscriptions(testUser.getId()).isEmpty());

        followerDAO.addFollower(testUser.getId(), user2.getId());
        assertThat(followerDAO.getAllFollowers(testUser.getId()).size(), is(2));
        assertThat(followerDAO.getAllFollowers(testUser.getId()).get(0),
                is(userDAO.getById(user1.getId()).get()));
        assertThat(followerDAO.getAllFollowers(testUser.getId()).get(1),
                is(userDAO.getById(user2.getId()).get()));
        assertTrue(followerDAO.getAllSubscriptions(testUser.getId()).isEmpty());

        assertThat(followerDAO.getAllSubscriptions(user1.getId()).get(0),
                is(userDAO.getById(testUser.getId()).get()));
        assertThat(followerDAO.getAllSubscriptions(user2.getId()).get(0),
                is(userDAO.getById(testUser.getId()).get()));

        followerDAO.addFollower(user1.getId(), user2.getId());
        assertThat(followerDAO.getAllSubscriptions(user2.getId()).get(0),
                is(userDAO.getById(testUser.getId()).get()));
        assertThat(followerDAO.getAllSubscriptions(user2.getId()).get(1),
                is(userDAO.getById(user1.getId()).get()));

        userDAO.deleteById(user1.getId());
        userDAO.deleteById(user2.getId());
        userDAO.deleteById(testUser.getId());
    }

}