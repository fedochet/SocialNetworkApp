package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.PostDAO;
import model.Post;
import model.PostPrivacyType;

import java.sql.*;
import java.time.Instant;
import java.util.*;

import static utils.GeneralUtils.mapOrElse;

/**
 * Created by roman on 08.07.2016.
 */
public class H2PostDAO implements PostDAO {
    private final ConnectionPool connectionPool;

    public H2PostDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private void setUpPost(PreparedStatement statement, Post post) throws SQLException {
        statement.setInt(1, post.getAuthorId());
        statement.setTimestamp(2, mapOrElse(post.getCreationTime(), Timestamp::from, Timestamp.from(Instant.now())));
        statement.setString(3, post.getText());
        statement.setInt(4, mapOrElse(post.getPostPrivacyType(), PostPrivacyType::getId, PostPrivacyType.DEFAULT.getId()));
    }

    private Post parsePost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getInt("id"));
        post.setAuthorId(resultSet.getInt("author_id"));
        post.setCreationTime(resultSet.getTimestamp("creation_time").toInstant());
        post.setText(resultSet.getString("text"));
        post.setPostPrivacyType(PostPrivacyType.getTypeByID(resultSet.getInt("post_privacy_type")));

        return post;
    }

    private Optional<Post> parsePostOpt(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) return Optional.of(parsePost(resultSet));
        else return Optional.empty();
    }

    private List<Post> parsePosts(ResultSet resultSet) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (resultSet.next()) {
            posts.add(parsePost(resultSet));
        }

        return Collections.unmodifiableList(posts);
    }

    @Override
    public List<Post> getByAuthorId(int id) {
        String sql = "SELECT id, author_id, creation_time, text, post_privacy_type FROM posts "
                + "WHERE author_id=? "
                + "ORDER BY creation_time DESC, id DESC";

        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                return parsePosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getByAuthorId(int authorId, int offsetId, int limit, int minId) {
        String sql = "SELECT id, author_id, creation_time, text, post_privacy_type FROM posts "
                + "WHERE author_id=? ";

        Map<String, Integer> positions = new HashMap<>();
        positions.put("authorId", 1);

        if (offsetId!=-1) {
            sql += "AND id <= ? ";
            positions.put("offsetId", positions.size()+1);
        }

        if (minId!=-1) {
            sql += "AND id > ? ";
            positions.put("minId", positions.size()+1);
        }

        sql += "ORDER BY creation_time DESC, id DESC LIMIT ?";
        positions.put("limit", positions.size()+1);

        try (
                Connection c = connectionPool.getConnection();
                PreparedStatement statement = c.prepareStatement(sql)
        ) {
            statement.setInt(1, authorId);

            if (offsetId!=-1) {
                statement.setInt(positions.get("offsetId"), offsetId);
            }

            if (minId!=-1) {
                statement.setInt(positions.get("minId"), minId);
            }

            statement.setInt(positions.get("limit"), limit);

            try(ResultSet resultSet = statement.executeQuery()) {
                return parsePosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int create(Post post) {
        String sql = "INSERT INTO posts(author_id, creation_time, text, post_privacy_type) "+
                "VALUES (?,?,?,?)";

        try (
            Connection c = connectionPool.getConnection();
            PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            setUpPost(statement, post);
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
                return parsePostOpt(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Post post) {
        String sql = "UPDATE posts SET author_id=?, creation_time=?, text=?, post_privacy_type=? "
                + "WHERE id=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setUpPost(statement, post);

            statement.setInt(5, post.getId());
            return statement.executeUpdate()>0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
