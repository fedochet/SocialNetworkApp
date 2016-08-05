package dao.interfaces;

import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by roman on 06.07.2016.
 */
public interface UserDAO extends AbstractDAO<User>{
    Optional<User> getByUsername(String username);

    boolean addFollower(int userId, int followerId);
    boolean removeFollower(int userId, int followerId);
    boolean isFollowing(int userId, int followerId);

    List<User> getAllFollowers(int userId);
    List<User> getAllSubscriptions(int userId);
}
