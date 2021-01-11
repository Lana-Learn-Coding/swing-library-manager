package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.spi.BookBorrowingRepo;
import org.springframework.stereotype.Component;

@Component
public class TicketDataCenterImpl extends AbstractRepositoryDataCenter<Integer, Ticket> implements TicketDataCenter {
    public TicketDataCenterImpl(BookBorrowingRepo repo) {
        super(repo);
    }
}
