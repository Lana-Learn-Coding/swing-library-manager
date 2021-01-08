package io.lana.library.core.spi;

import io.lana.library.core.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepo extends JpaRepository<Book, Integer> {
    List<Book> findAllByBorrowingIsNullAndIdIn(List<Integer> idList);
}
