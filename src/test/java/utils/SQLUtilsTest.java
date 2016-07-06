package utils;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

/**
 * Created by roman on 07.07.2016.
 */
public class SQLUtilsTest {

    private Path getTestScript(int number) {
        String fileName = number + ".sql";
        return Paths.get("src", "test", "resources", "testSqlScripts", fileName);
    }

    @Test
    public void testSqlScriptParsingOne() {
        Path testSqlScript = getTestScript(1);
        String[] testStatements = SQLUtils.getStatementsFromScript(testSqlScript);
        assertThat(testStatements.length, is(1));
        assertThat(testStatements[0], is("SELECT FROM user(ii,jj,kk)VALUES (ss, bb, dd)"));
    }

    @Test
    public void testSqlScriptParsingTwo() {
        Path testSqlScript = getTestScript(2);
        String[] testStatements = SQLUtils.getStatementsFromScript(testSqlScript);
        assertThat(testStatements.length,is(5));
        assertThat(testStatements, is(new String[]{"INSERT","INTO", "TABLE", "TEST TEST TEST", "TEST"}));
    }

    @Test
    public void testSqlScriptParsingThree() {
        Path testSqlScript = getTestScript(3);
        String[] testStatements = SQLUtils.getStatementsFromScript(testSqlScript);
        assertThat(testStatements.length,is(7));
        assertThat(testStatements[0], is("DROP TABLE IF EXISTS users"));
        assertThat(testStatements[4], is("CREATE TABLE post_types(id INT PRIMARY KEY,name VARCHAR(255))"));
    }

    @Test
    public void testSqlScriptParsingFour() {
        Path testSqlScript = getTestScript(4);
        String[] testStatements = SQLUtils.getStatementsFromScript(testSqlScript);
        assertThat(testStatements.length,is(37));
    }

}