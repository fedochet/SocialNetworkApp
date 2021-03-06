package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import model.User;
import model.UserRole;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.SQLUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static utils.TestsUtils.getTestUser;

/**
 * Created by roman on 08.07.2016.
 */
public class H2UserDAOTest {
    private static ConnectionPool connectionPool;
    private static H2UserDAO userDAO;
    private static String RESOURCES_DIR = "src/test/resources/";
    private static String DB_SCRIPT = "001_initial_db_structure.sql";
    private static String DB_PROPERTIES = "db.properties";

    @BeforeClass
    public static void beforeClass() {
        connectionPool = SimpleConnectionPool.create(RESOURCES_DIR + DB_PROPERTIES);
        userDAO = new H2UserDAO(connectionPool);
        SQLUtils.executeScript(connectionPool, RESOURCES_DIR + DB_SCRIPT);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connectionPool.close();
    }

    @Test
    public void userCreationAndDeletion() {
        User testUser = getTestUser();

        Optional<User> empty = userDAO.getByUsername(testUser.getUsername());
        assertThat(empty.isPresent(), is(false));

        int testId = userDAO.create(testUser);

        assertNotNull(testId);
        testUser.setId(testId);

        Optional<User> nonEmpty1 = userDAO.getById(testId);
        assertTrue(nonEmpty1.isPresent());
        assertThat(nonEmpty1.get(),is(testUser));

        Optional<User> nonEmpty2 = userDAO.getByUsername(testUser.getUsername());
        assertTrue(nonEmpty2.isPresent());
        assertThat(nonEmpty2.get(), is(testUser));

        assertTrue(userDAO.deleteById(testId));
        assertFalse(userDAO.getById(testId).isPresent());
        assertFalse(userDAO.deleteById(testId));
    }

    @Test
    public void ifRegistrationDateIsNullItsInserted(){
        User testUser = getTestUser();
        testUser.setRegistrationTime(null);

        int testId = userDAO.create(testUser);
        Optional<User> userOpt = userDAO.getById(testId);
        assertTrue(userOpt.isPresent());
        assertNotNull(userOpt.get().getRegistrationTime());
        assertTrue(userOpt.get().getRegistrationTime().getEpochSecond()<=Instant.now().getEpochSecond());

        userDAO.deleteById(testId);
    }

    @Test
    public void firstAndLastNamesCanBeNull() {
        User testUser = getTestUser();
        testUser.setFirstName(null);
        testUser.setLastName(null);

        int testId = userDAO.create(testUser);

        Optional<User> userOpt = userDAO.getById(testId);
        assertTrue(userOpt.isPresent());
        assertNull(userOpt.get().getFirstName());
        assertNull(userOpt.get().getLastName());

        userDAO.deleteById(testId);
    }

    @Test
    public void infoCanBeNullOnCreate() {
        User testUser = getTestUser();
        testUser.setInfo(null);

        int testId = userDAO.create(testUser);
        testUser.setId(testId);

        Optional<User> userOpt = userDAO.getById(testId);
        assertTrue(userOpt.isPresent());
        assertThat(userOpt.get(),is(testUser));

        userDAO.deleteById(testId);
    }

    @Test
    public void ifRoleIsNullItsSetToUser() {
        User testUser = getTestUser();
        testUser.setRole(null);

        int testId = userDAO.create(testUser);
        testUser.setId(testId);

        Optional<User> userOpt = userDAO.getById(testId);
        assertTrue(userOpt.isPresent());
        assertThat(userOpt.get().getRole(), is(UserRole.USER));

        userDAO.deleteById(testId);
    }

    @Test
    public void birthDateCanBeNull() {
        User testUser = getTestUser();
        testUser.setBirthDate(null);

        int testId = userDAO.create(testUser);

        Optional<User> userOpt = userDAO.getById(testId);
        assertTrue(userOpt.isPresent());
        assertNull(userOpt.get().getBirthDate());

        userDAO.deleteById(testId);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void usernameCannotBeNullOnInsert() {
        User testUser = getTestUser();
        testUser.setUsername(null);

        thrown.expect(RuntimeException.class);
        userDAO.create(testUser);
    }

    @Test
    public void usernameCannotBeNullOnUpdate() {
        User testUser = getTestUser();
        int testId = userDAO.create(testUser);
        testUser.setId(testId);
        testUser.setUsername(null);

        try {
            userDAO.update(testUser);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        userDAO.deleteById(testId);
    }

    @Test
    public void passwordCannotBeNullOnInsert() {
        User testUser = getTestUser();
        testUser.setPassword(null);

        thrown.expect(RuntimeException.class);
        userDAO.create(testUser);
    }

    @Test
    public void passwordCannotBeNullOnUpdate() {
        User testUser = getTestUser();
        int testId = userDAO.create(testUser);
        testUser.setId(testId);
        testUser.setPassword(null);

        try {
            userDAO.update(testUser);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        userDAO.deleteById(testId);
    }

    @Test
    public void updateTest() {
        User user = getTestUser();
        int testId = userDAO.create(user);
        user.setId(testId);
        assertTrue(userDAO.update(user));

        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setBirthDate(LocalDate.now());
        user.setPassword("testPassword");

        assertTrue(userDAO.update(user));

        Optional<User> updatedUserOpt = userDAO.getById(testId);

        assertTrue(updatedUserOpt.isPresent());
        assertThat(updatedUserOpt.get(), is(user));

        userDAO.deleteById(testId);
    }

    @Test
    public void updatingNonExistingUserReturnsFalse() {
        User testUser = getTestUser();
        testUser.setId(-1);
        assertFalse(userDAO.update(testUser));
    }

    @Test
    public void youCanGetUsersWithOffsetIdAndLimit(){
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pwd");
        user1.setId(userDAO.create(user1));

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pwd");
        user2.setId(userDAO.create(user2));

        User user3 = new User();
        user3.setUsername("user3");
        user3.setPassword("pwd");
        user3.setId(userDAO.create(user3));

        assertThat(userDAO.getUsers(-1, 100).size(), is(3));
        assertThat(userDAO.getUsers(-1, 1).size(), is(1));
        assertThat(userDAO.getUsers(-1, 2).size(), is(2));

        assertThat("Wrong order!", userDAO.getUsers(-1, 3).stream()
                        .sorted(Comparator.comparing(User::getId))
                        .collect(Collectors.toList()), is(userDAO.getUsers(-1, 3)));

        assertThat(userDAO.getUsers(user1.getId(), 100).size(), is(3));
        assertThat(userDAO.getUsers(user2.getId(), 100).size(), is(2));
        assertThat(userDAO.getUsers(user3.getId(), 100).size(), is(1));

        assertThat("Limit and offset combined are not working!",
                userDAO.getUsers(user2.getId(), 1).size(), is(1));

        userDAO.deleteById(user1.getId());
        userDAO.deleteById(user2.getId());
        userDAO.deleteById(user3.getId());
    }
}