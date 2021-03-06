package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.LikeDAO;
import model.User;

import java.sql.*;
import java.util.List;

import static utils.DAOUtils.parseUsers;

/**
 * Created by roman on 06.08.2016.
 */
public class H2LikeDAO implements LikeDAO {
    private final ConnectionPool connectionPool;

    public H2LikeDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public boolean addLike(int postId, int userId) {
        if (hasLike(postId, userId)) return false;

        String sql = "INSERT INTO likes(post_id, user_id) " +
                "VALUES (?, ?)";

        try(
            Connection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, postId);
            statement.setInt(2, userId);
            statement.execute();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return true;

                throw new SQLException("Creating like record failed; no ID returned!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeLike(int postId, int userId) {
        if (!hasLike(postId, userId)) return false;

        String sql = "DELETE FROM likes WHERE post_id=? AND user_id=?";
        try(
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);
            statement.setInt(2, userId);

            return statement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasLike(int postId, int userId) {
        String sql = "SELECT id FROM likes WHERE post_id=? AND user_id=?";

        try(
            Connection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllLikedUsers(int postId) {
        String sql = "SELECT id, username, password, first_name, last_name, info, birth_date, registration_time, role " +
                "FROM users WHERE id IN (SELECT user_id FROM likes WHERE post_id=?) " +
                "ORDER BY id ASC";


        try(
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return parseUsers(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
