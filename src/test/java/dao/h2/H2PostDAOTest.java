package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.Post;
import model.PostPrivacyType;
import model.User;
import org.junit.*;
import utils.SQLUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by roman on 08.07.2016.
 */
public class H2PostDAOTest {
    private static ConnectionPool connectionPool;
    private static H2UserDAO userDAO;
    private static H2PostDAO postDAO;
    private static String RESOURCES_DIR = "src/test/resources/";
    private static String DB_SCRIPT = "001_initial_db_structure.sql";
    private static String DB_PROPERTIES = "db.properties";

    @BeforeClass
    public static void beforeClass() {
        connectionPool = SimpleConnectionPool.create(RESOURCES_DIR + DB_PROPERTIES);
        userDAO = new H2UserDAO(connectionPool);
        postDAO = new H2PostDAO(connectionPool);
        SQLUtils.executeScript(connectionPool, RESOURCES_DIR + DB_SCRIPT);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connectionPool.close();
    }

    private User getTestUser() {
        User testUser = new User();
        testUser.setUsername("TestUser");
        testUser.setFirstName("roman");
        testUser.setLastName("golyshev");
        testUser.setPassword("test");
        testUser.setBirthDate(LocalDate.now());
        testUser.setRegistrationTime(Instant.now());

        return testUser;
    }

    private User testUser = getTestUser();

    @Before
    public void before() {
        User user = getTestUser();
        int userId = userDAO.create(user);
        testUser.setId(userId);
    }

    @After
    public void after() {
        userDAO.deleteById(testUser.getId());
    }

    @Test
    public void postAppearsAfterAdditionAndDisappearsAfterDeletion() {
        Post post = new Post();
        post.setAuthorId(testUser.getId());
        post.setPostPrivacyType(PostPrivacyType.DEFAULT);
        int postId = postDAO.create(post);

        Optional<Post> postOpt = postDAO.getById(postId);
        assertTrue(postOpt.isPresent());
        assertThat(postOpt.get().getId(), is(postId));
        assertThat(postOpt.get().getAuthorId(), is(testUser.getId()));
        assertThat(postOpt.get().getPostPrivacyType(), is(PostPrivacyType.DEFAULT));

        assertTrue(postDAO.deleteById(postId));
        assertFalse(postDAO.getById(postId).isPresent());
    }
}