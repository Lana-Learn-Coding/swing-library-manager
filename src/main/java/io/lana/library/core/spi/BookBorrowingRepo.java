package io.lana.library.core.spi;

import io.lana.library.core.model.book.BookBorrowing;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookBorrowingRepo extends PagingAndSortingRepository<BookBorrowing, Integer> {
}
