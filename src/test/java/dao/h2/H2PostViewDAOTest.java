package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.Post;
import model.PostView;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.SQLUtils;
import utils.TestsUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by roman on 07.08.2016.
 */
public class H2PostViewDAOTest {

    private static ConnectionPool connectionPool;
    private static H2UserDAO userDAO;
    private static H2PostDAO postDAO;
    private static H2LikeDAO likeDAO;
    private static H2PostViewDAO postViewDAO;
    private static String RESOURCES_DIR = "src/test/resources/";
    private static String DB_SCRIPT = "001_initial_db_structure.sql";
    private static String DB_PROPERTIES = "db.properties";

    @BeforeClass
    public static void beforeClass() {
        connectionPool = SimpleConnectionPool.create(RESOURCES_DIR + DB_PROPERTIES);
        userDAO = new H2UserDAO(connectionPool);
        postDAO = new H2PostDAO(connectionPool);
        likeDAO = new H2LikeDAO(connectionPool);

        postViewDAO = new H2PostViewDAO(connectionPool);
        SQLUtils.executeScript(connectionPool, RESOURCES_DIR + DB_SCRIPT);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connectionPool.close();
    }

    @Test
    public void youCanGetPostViews() {
        User testUser = TestsUtils.getTestUser();
        testUser.setId(userDAO.create(testUser));

        assertTrue(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).isEmpty());

        Post post = new Post();
        post.setAuthorId(testUser.getId());
        post.setId(postDAO.create(post));
        post = postDAO.getById(post.getId()).get();

        PostView expectedPost = new PostView();
        expectedPost.setPostId(post.getId());
        expectedPost.setPostText(post.getText());
        expectedPost.setPostCreationTime(post.getCreationTime());
        expectedPost.setAuthorId(post.getAuthorId());
        expectedPost.setAuthorUsername(testUser.getUsername());
        expectedPost.setAuthorFirstname(testUser.getFirstName());
        expectedPost.setAuthorLastname(testUser.getLastName());
        expectedPost.setLikes(0);
        expectedPost.setCanLike(true);

        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).size(),
                is(1));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(0),
            is(expectedPost));

        likeDAO.addLike(post.getId(), testUser.getId());
        expectedPost.setLikes(1);
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(0),
                is(expectedPost));

        expectedPost.setCanLike(false);
        assertThat(postViewDAO.getAsUserByAuthorId(testUser.getId(), testUser.getId(), -1, 100).get(0),
                is(expectedPost));

        userDAO.deleteById(testUser.getId());
    }

}