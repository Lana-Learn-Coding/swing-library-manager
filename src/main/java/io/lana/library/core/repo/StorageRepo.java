package io.lana.library.core.repo;

import io.lana.library.core.model.book.Storage;
import org.springframework.data.repository.CrudRepository;

public interface StorageRepo extends CrudRepository<Storage, Integer> {
}
