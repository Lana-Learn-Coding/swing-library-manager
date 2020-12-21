package io.lana.library.core.repo;

import io.lana.library.core.model.book.Series;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SeriesRepo extends PagingAndSortingRepository<Series, Integer> {
}
