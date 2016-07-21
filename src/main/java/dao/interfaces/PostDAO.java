package dao.interfaces;

import model.Post;

import java.util.List;

/**
 * Created by roman on 06.07.2016.
 */
public interface PostDAO extends AbstractDAO<Post>{

    /**
     * Returns limit or less number of posts ordered by specified author
     * from new to old (if same then ordered by ascending id)
     * @param id post author id
     * @return immutable list of posts
     */
    List<Post> getByAuthorId(int id);

    /**
     * Returns limit or less number of posts by specified author
     * starting from offset position (included) counting from 0
     * with same sort
     * @param id post author id
     * @param offset position to start from
     * @param limit maximum amount of returned posts
     * @return immutable list of posts
     */
    List<Post> getByAuthorId(int id, int offset, int limit);
}