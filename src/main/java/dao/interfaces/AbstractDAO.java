package dao.interfaces;

import java.util.Optional;

/**
 * Created by roman on 06.07.2016.
 */
public interface AbstractDAO<T> {
    int create(T model);
    Optional<T> getById(int id);
    boolean update(T model);
    boolean deleteById(int id);
}
