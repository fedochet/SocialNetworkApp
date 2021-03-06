package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.UserDAO;
import model.User;
import model.UserRole;
import utils.DAOUtils;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static utils.DAOUtils.parseUserOpt;
import static utils.GeneralUtils.mapOrElse;
import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 08.07.2016.
 */
public class H2UserDAO implements UserDAO {
    private final ConnectionPool connectionPool;

    public H2UserDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private int setUpUser(PreparedStatement statement, User model) throws SQLException {
        statement.setString(1, model.getUsername());
        statement.setString(2, model.getPassword());
        statement.setString(3, model.getFirstName());
        statement.setString(4, model.getLastName());
        statement.setString(5, model.getInfo());
        statement.setDate(6, mapOrNull(model.getBirthDate(), Date::valueOf));
        statement.setTimestamp(7, mapOrElse(model.getRegistrationTime(), Timestamp::from, Timestamp.from(Instant.now())));
        statement.setInt(8, mapOrElse(model.getRole(), UserRole::getId, UserRole.USER.getId()));

        return 9;
    }

    @Override
    public Optional<User> getByUsername(String username) {
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users WHERE username=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUserOpt(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getUsers(int offsetId, int limit) {
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users ";
        if (offsetId != -1) {
            sql += "WHERE id >= ? ";
        }
        sql += "LIMIT ?";

        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            if (offsetId!=-1) {
                statement.setInt(1, offsetId);
                statement.setInt(2, limit);
            } else {
                statement.setInt(1, limit);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return DAOUtils.parseUsers(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int create(User model) {
        String sql = "INSERT INTO " +
                "users(username, password,first_name, last_name, info, birth_date,registration_time, role)" +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            setUpUser(statement, model);
            statement.executeUpdate();

            try (ResultSet generated = statement.getGeneratedKeys()) {
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
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users WHERE id=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUserOpt(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(User model) {
        String sql = "UPDATE users SET "
                + "username=?, password=?, first_name=?, last_name=?, info=?, birth_date=?, registration_time=?, role=? "
                + "WHERE id=?";

        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            int nextIndex = setUpUser(statement, model);
            statement.setInt(nextIndex, model.getId());
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
