package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import dao.interfaces.UserDAO;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.SQLUtils;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
    public void usernameCannotBeNull() {
        User testUser = getTestUser();
        testUser.setUsername(null);

        thrown.expect(RuntimeException.class);
        userDAO.create(testUser);
    }

    @Test
    public void passwordCannotBeNull() {
        User testUser = getTestUser();
        testUser.setPassword(null);

        thrown.expect(RuntimeException.class);
        userDAO.create(testUser);
    }

}