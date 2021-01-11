package io.lana.library.core.spi.datacenter.base;

import io.lana.library.core.model.base.Identified;
import io.lana.library.utils.WorkerUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractRepositoryDataCenter<ID, T extends Identified<ID>> extends AbstractDataCenter<T>
    implements RepositoryDataCenter<ID, T> {
    protected final JpaRepository<T, ID> repo;
    protected Map<ID, T> internalData = new LinkedHashMap<>();
    protected List<ID> internalList = new ArrayList<>();

    public AbstractRepositoryDataCenter(JpaRepository<T, ID> repo) {
        this.repo = repo;
    }

    @Override
    public Stream<T> stream() {
        return internalData.values().stream();
    }

    @Override
    public Iterator<T> iterator() {
        return internalData.values().iterator();
    }

    @Override
    public int count() {
        return internalData.size();
    }

    @Override
    public void load(Collection<T> data) {
        internalList = new ArrayList<>(data.size() * 2);
        internalData = new HashMap<>(data.size() * 2);
        data.forEach(item -> {
            internalData.put(item.getId(), item);
            internalList.add(item.getId());
        });
    }

    @Override
    public void load() {
        load(repo.findAll());
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(internalData.values());
    }

    @Override
    public T findById(ID id) {
        return internalData.get(id);
    }

    public T findByIndex(int index) {
        ID id = internalList.get(index);
        return internalData.get(id);
    }

    @Override
    public int indexOf(ID id) {
        return internalList.indexOf(id);
    }

    @Override
    public void saveAll(Iterable<T> entities) {
        entities.forEach(this::save);
    }

    @Override
    public void updateAll(Iterable<T> entities) {
        entities.forEach(entity -> {
            T oldData = internalData.get(entity.getId());
            if (oldData == null) {
                throw new RuntimeException("ID not found: " + entity.getId());
            }
            internalData.put(entity.getId(), entity);
            updateSink.tryEmitNext(new DataChange<>(oldData, entity));
        });

        WorkerUtils.runAsync(() -> {
            entities.forEach(entity -> {
                T oldData = internalData.get(entity.getId());
                repo.save(entity);
                updatedSink.tryEmitNext(new DataChange<>(oldData, entity));
            });
        });
    }

    @Override
    public void save(T entity) {
        repo.save(entity);
        saveSink.tryEmitNext(entity);
        internalData.put(entity.getId(), entity);
        internalList.add(entity.getId());
        savedSink.tryEmitNext(entity);
    }

    @Override
    public void refresh(ID id) {
        T refreshData = internalData.get(id);
        updateSink.tryEmitNext(DataChange.unchanged(refreshData));
        updatedSink.tryEmitNext(DataChange.unchanged(refreshData));
    }

    @Override
    public void update(T entity) {
        T oldData = internalData.get(entity.getId());
        if (oldData == null) {
            throw new RuntimeException("ID not found: " + entity.getId());
        }
        updateSink.tryEmitNext(new DataChange<>(oldData, entity));
        internalData.put(entity.getId(), entity);
        WorkerUtils.runAsync(() -> {
            repo.save(entity);
            updatedSink.tryEmitNext(new DataChange<>(oldData, entity));
        });
    }

    @Override
    public void delete(ID id) {
        T oldData = internalData.get(id);
        deleteSink.tryEmitNext(oldData);
        internalData.remove(id);
        internalList.remove(id);
        WorkerUtils.runAsync(() -> {
            repo.deleteById(id);
            deletedSink.tryEmitNext(oldData);
        });
    }
}
