package io.lana.library.core.spi.datacenter;

import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.spi.repo.TicketRepo;
import org.springframework.stereotype.Component;

@Component
public class TicketDataCenterImpl extends AbstractRepositoryDataCenter<Integer, Ticket> implements TicketDataCenter {
    public TicketDataCenterImpl(TicketRepo repo) {
        super(repo);
    }
}
