package common.cp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by roman on 07.07.2016.
 */
public interface ConnectionPool extends AutoCloseable {
    Connection getConnection();
}
