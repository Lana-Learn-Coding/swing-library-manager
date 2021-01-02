package io.lana.library.core.spi;

import io.lana.library.core.model.book.Series;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SeriesRepo extends PagingAndSortingRepository<Series, Integer> {
    List<Series> findAll();

    boolean existsByName(String name);
}
