package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
    List<Category> findAllByOrderByUpdatedAtDesc();

    boolean existsByName(String name);
}
