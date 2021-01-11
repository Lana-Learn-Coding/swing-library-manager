package io.lana.library.core.spi.datacenter;

import io.lana.library.core.spi.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.book.Book;

import java.util.List;

public interface BookDataCenter extends RepositoryDataCenter<Integer, Book> {
    List<Book> findAllNotBorrowedAndIdIn(List<Integer> idList);
}
