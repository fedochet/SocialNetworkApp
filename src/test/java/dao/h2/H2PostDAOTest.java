package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.Post;
import model.PostPrivacyType;
import model.User;
import org.junit.*;
import org.junit.rules.ExpectedException;
import utils.SQLUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        post.setCreationTime(Instant.now());
        post.setText("post text");
        int postId = postDAO.create(post);

        post.setId(postId);

        Optional<Post> postOpt = postDAO.getById(postId);
        assertTrue(postOpt.isPresent());
        assertThat(postOpt.get(),is(post));

        assertTrue(postDAO.deleteById(postId));
        assertFalse(postDAO.getById(postId).isPresent());
    }

    @Test
    public void afterUpdatePostChanges() {
        Post post = new Post();
        post.setAuthorId(testUser.getId());
        post.setPostPrivacyType(PostPrivacyType.PRIVATE);
        post.setCreationTime(Instant.now());
        post.setText("post text");

        int postId = postDAO.create(post);
        post.setId(postId);

        testUser.setUsername("anotherUsername");
        int newUserId = userDAO.create(testUser);
        post.setText("other text");
        post.setCreationTime(Instant.now());
        post.setPostPrivacyType(PostPrivacyType.PROTECTED);
        post.setAuthorId(newUserId);

        assertTrue(postDAO.update(post));
        Optional<Post> postOpt = postDAO.getById(postId);
        assertTrue(postOpt.isPresent());
        assertThat(postOpt.get(), is(post));

        postDAO.deleteById(postId);
        userDAO.deleteById(newUserId);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void authorMustExist() {
        Post post = new Post();
        post.setPostPrivacyType(PostPrivacyType.PRIVATE);
        post.setCreationTime(Instant.now());
        post.setText("post text");
        post.setAuthorId(-1);

        thrown.expect(RuntimeException.class);
        postDAO.create(post);
    }

    @Test
    public void textCanBeNull() {
        Post post = new Post();
        post.setAuthorId(testUser.getId());
        post.setPostPrivacyType(PostPrivacyType.DEFAULT);
        post.setText(null);

        int postId = postDAO.create(post);
        assertTrue(postDAO.deleteById(postId));
    }

    @Test
    public void ifPrivacyIsNotSetItsDefault() {
        Post post = new Post();
        post.setPostPrivacyType(null);
        post.setCreationTime(Instant.now());
        post.setText("post text");
        post.setAuthorId(testUser.getId());

        int postId = postDAO.create(post);

        Optional<Post> postOpt = postDAO.getById(postId);
        assertTrue(postOpt.isPresent());
        assertThat(postOpt.get().getPostPrivacyType(), is(PostPrivacyType.DEFAULT));

        postDAO.deleteById(postId);
    }

    @Test
    public void ifCreationTimeNotSetItsNow() {
        Post post = new Post();
        post.setPostPrivacyType(PostPrivacyType.DEFAULT);
        post.setCreationTime(null);
        post.setText("post text");
        post.setAuthorId(testUser.getId());

        int postId = postDAO.create(post);

        Optional<Post> postOpt = postDAO.getById(postId);
        assertTrue(postOpt.isPresent());
        assertTrue(postOpt.get().getCreationTime().getEpochSecond()<=Instant.now().getEpochSecond());

        postDAO.deleteById(postId);
    }

    @Test
    public void youCanGetAllPostsOfUser() {
        User anotherUser = new User();
        anotherUser.setUsername("test_username");
        anotherUser.setPassword("test password");
        anotherUser.setId(userDAO.create(anotherUser));

        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        Post post4 = new Post();
        post1.setAuthorId(testUser.getId());
        post2.setAuthorId(testUser.getId());
        post3.setAuthorId(testUser.getId());
        post4.setAuthorId(anotherUser.getId());

        int postId1 = postDAO.create(post1);
        int postId2 = postDAO.create(post2);
        int postId3 = postDAO.create(post3);
        int postId4 = postDAO.create(post4);

        assertThat("Posts are in wrong order!", postDAO.getByAuthorId(testUser.getId())
                        .stream()
                        .sorted(Comparator.comparing(Post::getCreationTime).thenComparing(Post::getId))
                        .collect(Collectors.toList()),
                is(postDAO.getByAuthorId(testUser.getId())));

        assertThat(postDAO.getByAuthorId(testUser.getId()).size(), is(3));
        assertThat(postDAO.getByAuthorId(anotherUser.getId()).size(), is(1));

        postDAO.deleteById(postId1);
        assertThat(postDAO.getByAuthorId(testUser.getId()).size(), is(2));
        assertThat(postDAO.getByAuthorId(anotherUser.getId()).size(), is(1));

        postDAO.deleteById(postId4);
        assertThat(postDAO.getByAuthorId(testUser.getId()).size(), is(2));
        assertTrue(postDAO.getByAuthorId(anotherUser.getId()).isEmpty());

        postDAO.deleteById(postId2);
        postDAO.deleteById(postId3);
        assertTrue(postDAO.getByAuthorId(testUser.getId()).isEmpty());



        userDAO.deleteById(anotherUser.getId());
    }

    @Test
    public void youCanGetPostsWithOffsetAndLimit() {
        Set<Integer> newPosts = new HashSet<>();

        for (int i = 0; i<100; i++) {
            Post post = new Post();
            post.setAuthorId(testUser.getId());

            newPosts.add(postDAO.create(post));
        }

        List<Post> allPosts = postDAO.getByAuthorId(testUser.getId());
        assertThat(allPosts.size(), is(100));

        assertThat(postDAO.getByAuthorId(testUser.getId(), 0, 100),
                is(allPosts));

        List<Post> first50posts = postDAO.getByAuthorId(testUser.getId(), 0, 50);
        assertThat(first50posts.size(), is(50));
        assertThat(postDAO.getByAuthorId(testUser.getId(), 0, 50),
                is(allPosts.subList(0, 50)));

        assertThat(postDAO.getByAuthorId(testUser.getId(), 50, 20), is(allPosts.subList(50,70)));
        assertThat(postDAO.getByAuthorId(testUser.getId(), 80, 100), is(allPosts.subList(80,100)));
        assertThat(postDAO.getByAuthorId(testUser.getId(), 100, 1000).size(), is(0));
        assertThat(postDAO.getByAuthorId(testUser.getId(), 200, 1000).size(), is(0));

        assertThat("Posts are in wrong order!", postDAO.getByAuthorId(testUser.getId(),0, 100)
                .stream()
                .sorted(Comparator.comparing(Post::getCreationTime).thenComparing(Post::getId))
                .collect(Collectors.toList()),
            is(allPosts));

        newPosts.forEach(postDAO::deleteById);
    }
}