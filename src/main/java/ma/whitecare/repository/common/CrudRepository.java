package ma.whitecare.repository.common;


import java.util.List;

public interface CrudRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    void create(T newElement);

    void update(T newValuesElement);

    void delete(T oldElement);

    void deleteById(ID id);
}




