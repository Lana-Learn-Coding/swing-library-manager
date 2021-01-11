package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.BookMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookMetaRepo extends JpaRepository<BookMeta, Integer> {
    List<BookMeta> findAllByOrderByUpdatedAtDesc();
}
