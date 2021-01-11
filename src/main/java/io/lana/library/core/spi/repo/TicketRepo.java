package io.lana.library.core.spi.repo;

import io.lana.library.core.model.book.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Ticket, Integer> {
}
