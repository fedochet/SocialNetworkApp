package dao.h2;

import common.cp.ConnectionPool;
import common.cp.SimpleConnectionPool;
import dao.interfaces.UserDAO;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.SQLUtils;

import java.time.Instant;
import java.time.LocalDate;
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

    @Test
    public void userAppearsInDBAfterCreationAndDisappearsAfterDeletion() {
        String testUsername = "TestUser";
        Optional<User> empty = userDAO.getByUsername(testUsername);
        assertThat(empty.isPresent(), is(false));

        User testUser = new User();
        testUser.setUsername(testUsername);
        testUser.setPassword("test");
        testUser.setBirthDate(LocalDate.now());
        testUser.setRegistrationTime(Instant.now());
        int testId = userDAO.create(testUser);

        assertNotNull(testId);
        testUser.setId(testId);

        Optional<User> nonEmpty1 = userDAO.getById(testId);
        assertTrue(nonEmpty1.isPresent());
        assertThat(nonEmpty1.get(),is(testUser));

        Optional<User> nonEmpty2 = userDAO.getByUsername(testUsername);
        assertTrue(nonEmpty2.isPresent());
        assertThat(nonEmpty2.get(), is(testUser));

        assertTrue(userDAO.deleteById(testId));
        assertFalse(userDAO.getById(testId).isPresent());
        assertFalse(userDAO.deleteById(testId));
    }

}