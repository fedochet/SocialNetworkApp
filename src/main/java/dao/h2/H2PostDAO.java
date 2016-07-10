package dao.h2;

import dao.interfaces.PostDAO;
import common.cp.ConnectionPool;
import model.Post;
import model.PostPrivacyType;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static utils.GeneralUtils.mapOrElse;
import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 08.07.2016.
 */
public class H2PostDAO implements PostDAO {
    private final ConnectionPool connectionPool;

    public H2PostDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Post> getAllByAuthorId(int id) {
        return null;
    }

    @Override
    public List<Post> getAllByAuthorUsername(String username) {
        return null;
    }

    @Override
    public int create(Post post) {
        String sql = "INSERT INTO posts(author_id, creation_time, text, post_privacy_type) "+
                "VALUES (?,?,?,?)";

        try (
            Connection c = connectionPool.getConnection();
            PreparedStatement statement = c.prepareStatement(sql)
        ){
            statement.setInt(1, post.getAuthorId());
            statement.setTimestamp(2, mapOrElse(post.getCreationTime(), Timestamp::from, Timestamp.from(Instant.now())));
            statement.setString(3, post.getText());
            statement.setInt(4, mapOrElse(post.getPostPrivacyType(), PostPrivacyType::getId, PostPrivacyType.DEFAULT.getId()));

            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys()){
                if (set.next()) {
                    return set.getInt(1);
                } else throw new SQLException("Creating post failed: no ID generated");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Post> getById(int id) {
        String sql = "SELECT id, author_id, creation_time, text, post_privacy_type FROM posts " +
                "WHERE id=?";

        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1,id);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    Post post = new Post();
                    post.setId(resultSet.getInt("id"));
                    post.setAuthorId(resultSet.getInt("author_id"));
                    post.setCreationTime(resultSet.getTimestamp("creation_time").toInstant());
                    post.setText(resultSet.getString("text"));
                    post.setPostPrivacyType(PostPrivacyType.getTypeByID(resultSet.getInt("post_privacy_type")));

                    return Optional.of(post);
                } else {
                    return Optional.empty();
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Post model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM posts WHERE id=?";
        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            return statement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
