package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.Post;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.SQLUtils;
import utils.TestsUtils;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by roman on 06.08.2016.
 */
public class H2LikeDAOTest {

    private static ConnectionPool connectionPool;
    private static H2UserDAO userDAO;
    private static H2PostDAO postDAO;
    private static H2LikeDAO likeDAO;
    private static String RESOURCES_DIR = "src/test/resources/";
    private static String DB_SCRIPT = "001_initial_db_structure.sql";
    private static String DB_PROPERTIES = "db.properties";

    @BeforeClass
    public static void beforeClass() {
        connectionPool = SimpleConnectionPool.create(RESOURCES_DIR + DB_PROPERTIES);
        userDAO = new H2UserDAO(connectionPool);
        postDAO = new H2PostDAO(connectionPool);
        likeDAO = new H2LikeDAO(connectionPool);
        SQLUtils.executeScript(connectionPool, RESOURCES_DIR + DB_SCRIPT);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connectionPool.close();
    }

    @Test
    public void postAfterLikeHasLike() {
        User testUser = TestsUtils.getTestUser();
        testUser.setId(userDAO.create(testUser));

        Post testPost = new Post();
        testPost.setAuthorId(testUser.getId());
        testPost.setId(postDAO.create(testPost));

        assertFalse(likeDAO.hasLike(testPost.getId(), testUser.getId()));
        assertTrue(likeDAO.addLike(testPost.getId(), testUser.getId()));
        assertTrue(likeDAO.hasLike(testPost.getId(), testUser.getId()));

        assertFalse(likeDAO.addLike(testPost.getId(), testUser.getId()));

        assertTrue(likeDAO.removeLike(testPost.getId(), testUser.getId()));
        assertFalse(likeDAO.hasLike(testPost.getId(), testUser.getId()));
        assertFalse(likeDAO.removeLike(testPost.getId(), testUser.getId()));

        userDAO.deleteById(testUser.getId());
    }

    @Test
    public void youCanGetListOfUsersWhoLikedPost() {
        User user1 = TestsUtils.getTestUser();
        user1.setId(userDAO.create(user1));

        User user2 = TestsUtils.getFollower();
        user2.setId(userDAO.create(user2));

        Post post = new Post();
        post.setAuthorId(user1.getId());
        post.setId(postDAO.create(post));

        assertTrue(likeDAO.getAllLikedUsers(post.getId()).isEmpty());

        likeDAO.addLike(post.getId(), user1.getId());

        assertThat(likeDAO.getAllLikedUsers(post.getId()).get(0),
                is(userDAO.getById(user1.getId()).get()));

        likeDAO.addLike(post.getId(), user2.getId());

        assertThat(likeDAO.getAllLikedUsers(post.getId()).get(0),
                is(userDAO.getById(user1.getId()).get()));
        assertThat(likeDAO.getAllLikedUsers(post.getId()).get(1),
                is(userDAO.getById(user2.getId()).get()));


        userDAO.deleteById(user1.getId());
        userDAO.deleteById(user2.getId());
    }

}