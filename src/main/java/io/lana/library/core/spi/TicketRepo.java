package io.lana.library.core.spi;

import io.lana.library.core.model.book.BookMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<BookMeta, Integer> {
    List<BookMeta> findAllByOrderByUpdatedAtDesc();
}
