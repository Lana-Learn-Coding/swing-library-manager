package io.lana.library.core.repo;

import io.lana.library.core.model.book.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepo extends PagingAndSortingRepository<Category, Integer> {
}
