package io.lana.library.core.spi.datacenter;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.spi.datacenter.base.RepositoryDataCenter;

public interface BookDataCenter extends RepositoryDataCenter<Integer, Book> {
}
