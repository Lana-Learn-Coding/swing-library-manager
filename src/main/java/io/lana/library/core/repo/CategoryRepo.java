package io.lana.library.core.repo;

import io.lana.library.core.model.book.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Integer> {
}
