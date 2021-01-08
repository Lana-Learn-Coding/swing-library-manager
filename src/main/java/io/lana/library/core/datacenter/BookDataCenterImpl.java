package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.spi.BookRepo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDataCenterImpl extends AbstractRepositoryDataCenter<Integer, Book> implements BookDataCenter {
    public BookDataCenterImpl(BookRepo repo) {
        super(repo);
    }

    @Override
    public List<Book> findAllNotBorrowedAndIdIn(List<Integer> idList) {
        return ((BookRepo) repo).findAllByBorrowingIsNullAndIdIn(idList);
    }
}
