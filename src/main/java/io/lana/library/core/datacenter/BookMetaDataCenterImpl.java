package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.spi.TicketRepo;
import org.springframework.stereotype.Component;

@Component
public class BookMetaDataCenterImpl extends AbstractRepositoryDataCenter<Integer, BookMeta> implements BookMetaDataCenter {
    public BookMetaDataCenterImpl(TicketRepo repo) {
        super(repo);
    }
}
