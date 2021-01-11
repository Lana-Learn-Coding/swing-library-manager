package io.lana.library.core.spi.datacenter;

import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.spi.repo.BookMetaRepo;
import org.springframework.stereotype.Component;

@Component
public class BookMetaDataCenterImpl extends AbstractRepositoryDataCenter<Integer, BookMeta> implements BookMetaDataCenter {
    public BookMetaDataCenterImpl(BookMetaRepo repo) {
        super(repo);
    }
}
