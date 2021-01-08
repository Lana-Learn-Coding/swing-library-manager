package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.book.Book;

import java.util.List;

public interface BookDataCenter extends RepositoryDataCenter<Integer, Book> {
    List<Book> findAllNotBorrowedAndIdIn(List<Integer> idList);
}
