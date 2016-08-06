package dao.interfaces;

import model.Post;
import model.User;

import java.util.List;

/**
 * Created by roman on 06.08.2016.
 */
public interface LikeDAO {
    boolean addLike(Post post, User user);
    boolean removeLike(Post post, User user);
    boolean hasLike(Post post, User user);

    List<User> getAllLikedUsers(Post post);
}
