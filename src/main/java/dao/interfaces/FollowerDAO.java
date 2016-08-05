package dao.interfaces;

import model.User;

import java.util.List;

/**
 * Created by roman on 06.08.2016.
 */
public interface FollowerDAO {
    boolean addFollower(int userId, int followerId);
    boolean removeFollower(int userId, int followerId);
    boolean isFollowing(int userId, int followerId);

    List<User> getAllFollowers(int userId);
    List<User> getAllSubscriptions(int userId);
}
