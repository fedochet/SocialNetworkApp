package dao.h2;

import common.cp.ConnectionPool;
import dao.interfaces.PostViewDAO;
import model.PostView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by roman on 07.08.2016.
 */
public class H2PostViewDAO implements PostViewDAO {
    private final ConnectionPool connectionPool;

    public H2PostViewDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<PostView> getAsUserByAuthorId(int userId, int authorId, int offsetId, int limit) {
        String sql = "SELECT post_id, post_text, post_creation_time, " +
                "author_id, author_username, author_firstname, author_lastname, likes, " +
                "(SELECT count(likes.id) " +
                "FROM likes WHERE likes.post_id=post_id " +
                "AND likes.user_id=? GROUP BY likes.id) " +
                "AS canLike " +
                "FROM post_views " +
                "WHERE author_id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, authorId);
            try(ResultSet resultSet = statement.executeQuery()) {
                List<PostView> result = new ArrayList<>();

                while (resultSet.next()) {
                    PostView post = new PostView();

                    post.setPostId(resultSet.getInt("post_id"));
                    post.setPostText(resultSet.getString("post_text"));
                    post.setPostCreationTime(resultSet.getTimestamp("post_creation_time").toInstant());
                    post.setAuthorId(resultSet.getInt("author_id"));
                    post.setAuthorUsername(resultSet.getString("author_username"));
                    post.setAuthorFirstname(resultSet.getString("author_firstname"));
                    post.setAuthorLastname(resultSet.getString("author_lastname"));
                    post.setLikes(resultSet.getInt("likes"));
                    post.setCanLike(resultSet.getInt("canLike")==0);
                    result.add(post);
                }

                return Collections.unmodifiableList(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
