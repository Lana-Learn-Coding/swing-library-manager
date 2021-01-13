package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BookRepo extends JpaRepository<Book, Integer> {
    List<Book> findAllByIdIn(Collection<Integer> ids);
}
