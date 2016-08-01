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
     * starting from post with offsetID position (included) counting from 0
     * with same sort
     * @param id post author id
     * @param offsetId highest id of returned posts must be less or equal to this; if -1 - not used
     * @param limit maximum amount of returned posts
     * @param minId minimal id exclusive; if -1 - not used
     * @return immutable list of posts
     */
    List<Post> getByAuthorId(int id, int offsetId, int limit, int minId);

}