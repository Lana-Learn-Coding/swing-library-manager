package io.lana.library.core.service;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;

import java.util.Collection;
import java.util.List;

public interface BookService {
    void createBookMeta(BookMeta bookMeta);

    void updateBookMeta(BookMeta bookMeta);

    void deleteBookMeta(BookMeta bookMeta);

    void createBook(Book book);

    void updateBook(Book book);

    void deleteBook(Book book);

    List<Book> findAllNotBorrowedBookByIdIn(Collection<Integer> ids);
}
