package io.lana.library.core.repo;

import io.lana.library.core.model.book.Storage;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StorageRepo extends PagingAndSortingRepository<Storage, Integer> {
}
