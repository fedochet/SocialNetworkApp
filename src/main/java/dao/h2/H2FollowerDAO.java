package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.FollowerDAO;
import model.User;
import model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 06.08.2016.
 */
public class H2FollowerDAO implements FollowerDAO {
    private final ConnectionPool connectionPool;

    public H2FollowerDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private User parseUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setInfo(resultSet.getString("info"));
        user.setBirthDate(mapOrNull(resultSet.getDate("birth_date"), Date::toLocalDate));
        user.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant());
        user.setRole(UserRole.getRoleById(resultSet.getInt("role")));
        return user;
    }

    private List<User> parseUsers(ResultSet resultSet) throws SQLException {
        List<User> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(parseUser(resultSet));
        }

        return Collections.unmodifiableList(result);
    }


    @Override
    public boolean isFollowing(int userId, int followerId) {
        String sql = "SELECT id FROM user_followers WHERE user_id=? AND follower_id=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, followerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean addFollower(int userId, int followerId) {
        if (userId == followerId) return false;
        if (isFollowing(userId, followerId)) return false;

        String sql = "INSERT INTO user_followers(user_id, follower_id) " +
                "VALUES (?, ?)";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, followerId);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next())
                    return true;
                throw new SQLException("Adding follower failed: no ID generated");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeFollower(int userId, int followerId) {
        if (!isFollowing(userId, followerId)) return false;

        String sql = "DELETE FROM user_followers WHERE user_id=? AND follower_id=?";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ){
            statement.setInt(1, userId);
            statement.setInt(2, followerId);
            return statement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllFollowers(int userId) {
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users WHERE id IN (SELECT follower_id FROM user_followers WHERE user_id=?) " +
                "ORDER BY id ASC";

        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUsers(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllSubscriptions(int userId) {
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users WHERE id IN (SELECT user_id FROM user_followers WHERE follower_id=?) " +
                "ORDER BY id ASC";
        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUsers(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
