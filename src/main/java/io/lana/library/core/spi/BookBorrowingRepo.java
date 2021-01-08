package io.lana.library.core.spi;

import io.lana.library.core.model.book.BookBorrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowingRepo extends JpaRepository<BookBorrowing, Integer> {
}
