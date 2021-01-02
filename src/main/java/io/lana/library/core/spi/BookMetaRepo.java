package io.lana.library.core.spi;

import io.lana.library.core.model.book.BookMeta;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BookMetaRepo extends PagingAndSortingRepository<BookMeta, Integer> {
    List<BookMeta> findAllByOrderByUpdatedAtDesc();
}
