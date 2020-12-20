package io.lana.library.core.repo;

import io.lana.library.core.model.book.BookBorrowing;
import org.springframework.data.repository.CrudRepository;

public interface BookBorrowingRepo extends CrudRepository<BookBorrowing, Integer> {
}
