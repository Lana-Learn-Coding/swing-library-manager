package io.lana.library.core.spi.datacenter;

import io.lana.library.core.spi.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.book.Ticket;
import org.springframework.stereotype.Component;

@Component
public interface TicketDataCenter extends RepositoryDataCenter<Integer, Ticket> {
}
