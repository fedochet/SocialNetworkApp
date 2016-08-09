package dao.interfaces;

import model.PostView;

import java.util.List;

/**
 * Created by roman on 07.08.2016.
 */
public interface PostViewDAO {
    List<PostView> getAsUserByAuthorId(int userId, int authorId, int offsetId, int limit);

    List<PostView> getTimeline(int userId, int offsetId, int limit);
}
