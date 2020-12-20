package io.lana.library.core.repo;

import io.lana.library.core.model.book.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepo extends CrudRepository<Book, Integer> {
}
