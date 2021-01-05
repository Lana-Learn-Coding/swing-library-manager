package io.lana.library.core.spi;

import io.lana.library.core.model.book.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BookRepo extends PagingAndSortingRepository<Book, Integer> {
    List<Book> findAllByBorrowingIsNullAndIdIn(List<Integer> idList);
}
