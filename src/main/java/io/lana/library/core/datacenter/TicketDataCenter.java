package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.book.Ticket;
import org.springframework.stereotype.Component;

@Component
public interface TicketDataCenter extends RepositoryDataCenter<Integer, Ticket> {
}
