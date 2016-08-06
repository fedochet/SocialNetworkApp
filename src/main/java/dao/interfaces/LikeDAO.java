package dao.interfaces;

import model.User;

import java.util.List;

/**
 * Created by roman on 06.08.2016.
 */
public interface LikeDAO {
    boolean addLike(int postId, int userId);
    boolean removeLike(int postId, int userId);
    boolean hasLike(int postId, int userId);

    List<User> getAllLikedUsers(int postId);
}
