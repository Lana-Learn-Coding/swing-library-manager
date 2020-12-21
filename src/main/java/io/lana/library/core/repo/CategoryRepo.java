package io.lana.library.core.repo;

import io.lana.library.core.model.book.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepo extends PagingAndSortingRepository<Category, Integer> {
    List<Category> findAll();

    boolean existsByName(String name);
}
