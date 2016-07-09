package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.UserDAO;
import model.User;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * Created by roman on 08.07.2016.
 */
public class H2UserDAO implements UserDAO {
    private final ConnectionPool connectionPool;

    public H2UserDAO(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    private<T,B> B mapOrNull(T valueToCheck, Function<T,B> map) {
        return mapOrElse(valueToCheck, map, null);
    }

    private <T,B> B mapOrElse(T valueToCheck, Function<T,B> map, B defaultValue) {
        if (valueToCheck == null)
            return defaultValue;

        return map.apply(valueToCheck);
    }

    private Optional<User> parseUser(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setBirthDate(mapOrNull(resultSet.getDate("birth_date"), Date::toLocalDate));
                user.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant());
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> getByUsername(String username) {
        String sql = "SELECT id, username, password, first_name, last_name, birth_date, registration_time " +
                "FROM users WHERE username=?";
        try (
            Connection c = connectionPool.getConnection();
            PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> getPasswordByUsername(String username) {
        return null;
    }

    @Override
    public int create(User model) {
        String sql = "INSERT INTO " +
                "users(username, password,first_name, last_name, birth_date, registration_time) " +
                "VALUES (?,?,?,?,?,?)";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setString(1, model.getUsername());
            statement.setString(2, model.getPassword());
            statement.setString(3, model.getFirstName());
            statement.setString(4, model.getLastName());
            statement.setDate(5, mapOrNull(model.getBirthDate(), Date::valueOf));
            statement.setTimestamp(6, mapOrElse(model.getRegistrationTime(), Timestamp::from, Timestamp.from(Instant.now())));
            statement.executeUpdate();

            try (ResultSet generated = statement.getGeneratedKeys()){
                if (generated.next()) {
                    return generated.getInt(1);
                } else throw new SQLException("Creating user failed: no ID generated");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT id, username, password, first_name, last_name, birth_date, registration_time " +
                "FROM users WHERE id=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                return parseUser(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(User model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (
            Connection c = connectionPool.getConnection();
            PreparedStatement statement = c.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            return statement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
