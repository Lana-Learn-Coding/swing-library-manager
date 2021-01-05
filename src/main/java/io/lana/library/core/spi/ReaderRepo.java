package io.lana.library.core.spi;

import io.lana.library.core.model.Reader;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReaderRepo extends PagingAndSortingRepository<Reader, Integer> {
    List<Reader> findAllByOrderByUpdatedAtDesc();
}
