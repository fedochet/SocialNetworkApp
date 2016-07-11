package dao.interfaces;

import model.Post;

import java.util.List;

/**
 * Created by roman on 06.07.2016.
 */
public interface PostDAO extends AbstractDAO<Post>{
    List<Post> getAllByAuthorId(int id);
}