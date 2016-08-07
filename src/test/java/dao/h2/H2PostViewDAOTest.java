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

import java.util.List;
import java.util.Set;

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

        Post post1 = new Post();
        post1.setAuthorId(testUser.getId());
        post1.setId(postDAO.create(post1));
        post1 = postDAO.getById(post1.getId()).get();

        Post post2 = new Post();
        post2.setAuthorId(testUser.getId());
        post2.setId(postDAO.create(post2));
        post2 = postDAO.getById(post2.getId()).get();

        PostView expectedPost1 = new PostView();
        expectedPost1.setId(post1.getId());
        expectedPost1.setText(post1.getText());
        expectedPost1.setCreationTime(post1.getCreationTime());
        expectedPost1.setAuthorId(post1.getAuthorId());
        expectedPost1.setAuthorUsername(testUser.getUsername());
        expectedPost1.setAuthorFirstname(testUser.getFirstName());
        expectedPost1.setAuthorLastname(testUser.getLastName());
        expectedPost1.setLikes(0);
        expectedPost1.setCanLike(true);

        PostView expectedPost2 = new PostView();
        expectedPost2.setId(post2.getId());
        expectedPost2.setText(post2.getText());
        expectedPost2.setCreationTime(post2.getCreationTime());
        expectedPost2.setAuthorId(post2.getAuthorId());
        expectedPost2.setAuthorUsername(testUser.getUsername());
        expectedPost2.setAuthorFirstname(testUser.getFirstName());
        expectedPost2.setAuthorLastname(testUser.getLastName());
        expectedPost2.setLikes(0);
        expectedPost2.setCanLike(true);


        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).size(),
                is(2));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(1),
                is(expectedPost1));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(0),
                is(expectedPost2));

        likeDAO.addLike(post1.getId(), testUser.getId());
        expectedPost1.setLikes(1);
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(1),
                is(expectedPost1));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).get(0),
                is(expectedPost2));

        expectedPost1.setCanLike(false);
        assertThat(postViewDAO.getAsUserByAuthorId(testUser.getId(), testUser.getId(), -1, 100).get(1),
                is(expectedPost1));
        assertThat(postViewDAO.getAsUserByAuthorId(testUser.getId(), testUser.getId(), -1, 100).get(0),
                is(expectedPost2));

        userDAO.deleteById(testUser.getId());
    }

    @Test
    public void youCanGetPostViewsWithOffsetAndLimit() {
        User testUser = TestsUtils.getTestUser();
        testUser.setId(userDAO.create(testUser));

        Set<Integer> ids = TestsUtils.add100Posts(testUser, postDAO);
        List<Post> posts = postDAO.getByAuthorId(testUser.getId());

        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), -1, 100).size(), is(100));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), posts.get(50).getId(), 100).size(), is(50));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), posts.get(50).getId(), 10).size(), is(10));
        assertThat(postViewDAO.getAsUserByAuthorId(-1, testUser.getId(), posts.get(50).getId(), 10).get(0).getId(),
                is(posts.get(50).getId()));
        TestsUtils.deleteAllPostsById(ids, postDAO);
        userDAO.deleteById(testUser.getId());
    }

}