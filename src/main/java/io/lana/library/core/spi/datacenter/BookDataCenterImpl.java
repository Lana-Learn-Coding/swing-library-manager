package io.lana.library.core.spi.datacenter;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.spi.repo.BookRepo;
import org.springframework.stereotype.Component;

@Component
public class BookDataCenterImpl extends AbstractRepositoryDataCenter<Integer, Book> implements BookDataCenter {
    public BookDataCenterImpl(BookRepo repo) {
        super(repo);
    }
}
