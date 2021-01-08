package io.lana.library.core.datacenter.base;

import io.lana.library.core.model.base.Identified;

import java.util.Collection;
import java.util.List;

public interface RepositoryDataCenter<ID, T extends Identified<ID>> extends DataCenter<T> {
    void load();

    void load(Collection<T> data);

    List<T> findAll();

    int indexOf(ID id);

    T findById(ID id);

    T findByIndex(int index);

    default int indexOf(T entity) {
        return indexOf(entity.getId());
    }

    int count();

    void save(T entity);

    void saveAll(Iterable<T> entities);

    void refresh(ID id);

    default void refresh(T entity) {
        refresh(entity.getId());
    }

    void updateAll(Iterable<T> entities);

    void update(T entity);

    void delete(ID id);

    default void delete(T entity) {
        delete(entity.getId());
    }
}
