package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.BookBorrowing;
import io.lana.library.core.spi.BookBorrowingRepo;
import org.springframework.stereotype.Component;

@Component
public class BookBorrowingDataCenterImpl extends AbstractRepositoryDataCenter<Integer, BookBorrowing> implements BookBorrowingDataCenter {
    public BookBorrowingDataCenterImpl(BookBorrowingRepo repo) {
        super(repo);
    }
}
