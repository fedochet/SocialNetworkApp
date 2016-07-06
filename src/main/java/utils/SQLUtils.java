package utils;

import common.cp.ConnectionPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by roman on 07.07.2016.
 */
public interface SQLUtils {
    static void executeScript(ConnectionPool cp, String scriptPath) {
        executeScript(cp, Paths.get(scriptPath));
    }

    static void executeScript(ConnectionPool cp, Path scriptPath) {
        try (
                Connection connection = cp.getConnection();
                Statement statement = connection.createStatement()
        ) {

            String[] sqls = getStatementsFromScript(scriptPath);

            for (String sql : sqls) {
                statement.addBatch(sql);
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error while executing script", e);
        }
    }

    static String[] getStatementsFromScript(Path scriptPath){
        try {
            String[] sqls = Files.lines(scriptPath)
                    .filter(s->!s.isEmpty())
                    .map(s->!Character.isLetterOrDigit(s.charAt(s.length()-1))?s:s+" ")
                    .collect(Collectors.joining())
                    .split(";");

            return sqls;

        } catch (IOException e) {
            throw new RuntimeException("Exception while parsing script", e);
        }
    }
}
