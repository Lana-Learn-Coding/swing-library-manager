package io.lana.library.core.spi;

import io.lana.library.core.model.book.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepo extends PagingAndSortingRepository<Book, Integer> {
}
