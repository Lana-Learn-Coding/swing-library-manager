package io.lana.library.core.spi;

import io.lana.library.core.model.book.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowingRepo extends JpaRepository<Ticket, Integer> {
}
