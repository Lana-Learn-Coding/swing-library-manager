package io.lana.library.core.datacenter;

import io.lana.library.core.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.book.BookBorrowing;
import org.springframework.stereotype.Component;

@Component
public interface BookBorrowingDataCenter extends RepositoryDataCenter<Integer, BookBorrowing> {
}
