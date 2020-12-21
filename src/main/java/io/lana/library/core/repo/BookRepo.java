package io.lana.library.core.repo;

import io.lana.library.core.model.book.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepo extends PagingAndSortingRepository<Book, Integer> {
}
