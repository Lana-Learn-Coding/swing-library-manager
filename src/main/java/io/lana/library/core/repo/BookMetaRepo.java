package io.lana.library.core.repo;

import io.lana.library.core.model.book.BookMeta;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookMetaRepo extends PagingAndSortingRepository<BookMeta, Integer> {
}
