package dao.interfaces;

import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by roman on 06.07.2016.
 */
public interface UserDAO extends AbstractDAO<User>{
    Optional<User> getByUsername(String username);
    List<User> getUsers(int offsetId, int limit);
}
