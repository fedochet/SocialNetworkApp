package dao.interfaces;

import model.User;

import java.util.Optional;

/**
 * Created by roman on 06.07.2016.
 */
public interface UserDAO extends AbstractDAO<User>{
    Optional<User> getByUsername(String username);
}
