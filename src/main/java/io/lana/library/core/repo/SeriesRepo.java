package io.lana.library.core.repo;

import io.lana.library.core.model.book.Series;
import org.springframework.data.repository.CrudRepository;

public interface SeriesRepo extends CrudRepository<Series, Integer> {
}
