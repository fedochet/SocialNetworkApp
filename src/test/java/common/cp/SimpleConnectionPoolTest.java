package common.cp;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by roman on 15.07.2016.
 */
public class SimpleConnectionPoolTest {
    private static final String DB_PROPERTIES = "src/test/resources/db.properties";

    @Test
    public void testThatPropertyFileIsNotLockAfterCreation() throws Exception {
        File fileBefore = new File(DB_PROPERTIES);
        assertTrue(fileBefore.renameTo(fileBefore)); // dirty hack to check if file is released
        ConnectionPool pool1 = SimpleConnectionPool.create(DB_PROPERTIES);
        File fileAfter = new File(DB_PROPERTIES);
        assertTrue(fileAfter.renameTo(fileAfter)); // dirty hack to check if file is released
        pool1.close();
    }
}