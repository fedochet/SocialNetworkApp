package dao.h2;

import dao.interfaces.PostDAO;
import model.Post;

import java.util.List;
import java.util.Optional;

/**
 * Created by roman on 08.07.2016.
 */
public class H2PostDAO implements PostDAO {
    @Override
    public List<Post> getAllByAuthorId(int id) {
        return null;
    }

    @Override
    public List<Post> getAllByAuthorUsername(String username) {
        return null;
    }

    @Override
    public int create(Post model) {
        return 0;
    }

    @Override
    public Optional<Post> getById(int id) {
        return null;
    }

    @Override
    public boolean update(Post model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
