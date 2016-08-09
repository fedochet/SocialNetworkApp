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

    private PostView parsePostView(ResultSet resultSet) throws SQLException {
        PostView post = new PostView();

        post.setId(resultSet.getInt("post_id"));
        post.setText(resultSet.getString("post_text"));
        post.setCreationTime(resultSet.getTimestamp("post_creation_time").toInstant());
        post.setAuthorId(resultSet.getInt("author_id"));
        post.setAuthorUsername(resultSet.getString("author_username"));
        post.setAuthorFirstname(resultSet.getString("author_firstname"));
        post.setAuthorLastname(resultSet.getString("author_lastname"));
        post.setLikes(resultSet.getInt("post_likes"));
        post.setCanLike(resultSet.getInt("canLike")==0);

        return post;
    }

    private List<PostView> parsePostViews(ResultSet resultSet) throws SQLException {
        List<PostView> result = new ArrayList<>();

        while (resultSet.next()) {
            result.add(parsePostView(resultSet));
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<PostView> getAsUserByAuthorId(int userId, int authorId, int offsetId, int limit) {
        String sql = "SELECT post_id, post_text, post_creation_time, " +
                "author_id, author_username, author_firstname, author_lastname, post_likes, " +
                "(SELECT count(likes.id) " +
                "FROM likes WHERE likes.post_id=post_views.post_id " +
                "AND likes.user_id = ? GROUP BY likes.id) " +
                "AS canLike " +
                "FROM post_views " +
                "WHERE author_id = ? ";

        if (offsetId!=-1) {
            sql += "AND post_id <= ? ";
        }

        sql += "ORDER BY post_creation_time DESC, post_id DESC LIMIT ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, authorId);

            int limitPos = 3;
            if (offsetId != -1) {
                statement.setInt(3, offsetId);
                limitPos++;
            }

            statement.setInt(limitPos, limit);

            try(ResultSet resultSet = statement.executeQuery()) {
                return parsePostViews(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PostView> getTimeline(int userId, int offsetId, int limit) {
        String sql = "SELECT post_id, post_text, post_creation_time, " +
                "author_id, author_username, author_firstname, author_lastname, post_likes, " +
                "(SELECT count(likes.id) " +
                "FROM likes WHERE likes.post_id=post_views.post_id " +
                "AND likes.user_id = ? GROUP BY likes.id) " +
                "AS canLike " +
                "FROM post_views " +
                "WHERE author_id IN " +
                "(SELECT user_id FROM user_followers " +
                "WHERE follower_id = ?) " +
                "OR author_id = ? ";

        if (offsetId!=-1) {
            sql += "AND post_id <= ? ";
        }
        sql += "ORDER BY post_creation_time DESC, post_id DESC " +
                "LIMIT ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);

            int limitPosition = 4;
            if (offsetId != -1) {
                statement.setInt(4, offsetId);
                limitPosition++;
            }

            statement.setInt(limitPosition, limit);

            try(ResultSet resultSet = statement.executeQuery()) {
                return parsePostViews(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
