package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepo extends JpaRepository<Series, Integer> {
    List<Series> findAllByOrderByUpdatedAtDesc();

    boolean existsByName(String name);
}
