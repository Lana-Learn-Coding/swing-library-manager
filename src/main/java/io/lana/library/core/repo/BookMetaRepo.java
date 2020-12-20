package io.lana.library.core.repo;

import io.lana.library.core.model.book.BookMeta;
import org.springframework.data.repository.CrudRepository;

public interface BookMetaRepo extends CrudRepository<BookMeta, Integer> {
}
