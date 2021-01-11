package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepo extends JpaRepository<Storage, Integer> {
}
