package common.cp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

/**
 * Created by roman on 07.07.2016.
 */
public class SimpleConnectionPool implements ConnectionPool{

    private BlockingQueue<Connection> freeConnections;
    private BlockingQueue<Connection> reservedConnections;
    private volatile boolean isClosing = false;

    private static InputStream getInputStream(String file){
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionPool create(String propertiesFile) {
        return create(getInputStream(propertiesFile));
    }

    private static ConnectionPool create(InputStream stream) {
        Properties properties = new Properties();
        try {
            properties.load(stream);
            return create(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionPool create(Properties properties) {
        String user = properties.getProperty("user","");
        String password = properties.getProperty("password","");

        if (!properties.containsKey("driver") || !properties.containsKey("url"))
            throw new IllegalArgumentException("Cannot find driver or url property");

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");

        int poolsize = Integer.parseInt(properties.getProperty("poolSize", "1"));

        return create(driver, url, user, password, poolsize);
    }

    public static ConnectionPool create(String driver, String url, String user, String password, int poolsize) {
        return new SimpleConnectionPool(poolsize, ()-> {
            try {
                Class.forName(driver);
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private SimpleConnectionPool(int poolsize, Supplier<Connection> connectionSupplier) {
        freeConnections = new ArrayBlockingQueue<>(poolsize);
        reservedConnections = new ArrayBlockingQueue<>(poolsize);

        for (int i = 0; i<poolsize; i++) {
            freeConnections.add(connectionSupplier.get());
        }
    }

    private Connection proxy(Connection connection) {
        return new ConnectionProxy(connection) {
            @Override
            public void close() throws SQLException {
                if (connection.isClosed()) {
                    throw new SQLException("Connection is already closed");
                }

                if (connection.isReadOnly()) {
                    connection.setReadOnly(false);
                }

                if (reservedConnections.contains(this) && !reservedConnections.remove(this))
                    throw new RuntimeException("Error deleting connection from the given away connections pool.");

                if (isClosing) {
                    connection.close();
                } else if (!freeConnections.offer(connection)) {
                    throw new RuntimeException("Cannot return connection to pool");
                }
            }

        };
    }

    @Override
    public Connection getConnection() {

        if (isClosing) throw new IllegalStateException("Connection pool is closing");

        try {
            Connection connection = proxy(freeConnections.take());
            reservedConnections.add(connection);
            return connection;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        isClosing = true;
        freeConnections.forEach(connection -> {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
