package io.lana.library.core.service;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.datacenter.BookDataCenter;
import io.lana.library.core.spi.datacenter.BookMetaDataCenter;
import io.lana.library.core.spi.datacenter.ReaderDataCenter;
import io.lana.library.core.spi.datacenter.TicketDataCenter;
import io.lana.library.core.spi.repo.BookRepo;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookServiceImpl implements BookService {
    private BookMetaDataCenter bookMetaDataCenter;

    private BookDataCenter bookDataCenter;

    private TicketDataCenter ticketDataCenter;

    private ReaderDataCenter readerDataCenter;

    private FileStorage fileStorage;

    private BookRepo bookRepo;

    @Autowired
    public void setup(FileStorage fileStorage, BookMetaDataCenter bookMetaDataCenter,
                      BookDataCenter bookDataCenter, TicketDataCenter ticketDataCenter,
                      ReaderDataCenter readerDataCenter, BookRepo bookRepo) {
        this.bookDataCenter = bookDataCenter;
        this.fileStorage = fileStorage;
        this.bookMetaDataCenter = bookMetaDataCenter;
        this.ticketDataCenter = ticketDataCenter;
        this.readerDataCenter = readerDataCenter;
        this.bookRepo = bookRepo;
    }

    @Override
    public void createBookMeta(BookMeta bookMeta) {
        if (StringUtils.isNotBlank(bookMeta.getImage())) {
            String savedImage = fileStorage.loadFileToStorage(bookMeta.getImage());
            bookMeta.setImage(savedImage);
        }
        bookMetaDataCenter.save(bookMeta);
    }

    @Override
    public void updateBookMeta(BookMeta bookMeta) {
        BookMeta updated = bookMetaDataCenter.findById(bookMeta.getId());
        updated.setTitle(bookMeta.getTitle());
        updated.setYear(bookMeta.getYear());
        updated.setPublisher(bookMeta.getPublisher());
        updated.setAuthor(bookMeta.getAuthor());
        updated.setCategory(bookMeta.getCategory());
        updated.setSeries(bookMeta.getSeries());
        if (StringUtils.isNotBlank(bookMeta.getImage())) {
            String oldImage = updated.getImage();
            updated.setImage(fileStorage.loadFileToStorage(bookMeta.getImage()));
            WorkerUtils.runAsync(() -> fileStorage.deleteFileFromStorage(oldImage));
        }
        bookMetaDataCenter.update(updated);
        bookDataCenter.refreshAll(updated.getBooks());
    }

    @Override
    public void deleteBookMeta(BookMeta bookMeta) {
        bookMeta.setDeleted(true);
        bookMetaDataCenter.delete(bookMeta);
        Set<Book> books = bookMeta.getBooks();
        books.forEach(book -> {
            book.setDeleted(true);
            bookDataCenter.delete(book);
        });

        Set<Ticket> tickets = books.stream()
            .map(Book::getTickets)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        if (tickets.isEmpty()) {
            return;
        }

        tickets.stream()
            .filter(ticket -> ticket.getBooksCount() == 0)
            .forEach(ticket -> {
                ticket.setReturned(true);
                ticketDataCenter.update(ticket);
            });

        ticketDataCenter.refreshAll(tickets);
        Set<Reader> readers = tickets.stream().map(Ticket::getBorrower).collect(Collectors.toSet());
        readerDataCenter.refreshAll(readers);
    }

    @Override
    public void createBook(Book book) {
        BookMeta meta = book.getMeta();
        if (meta == null) {
            throw new ServiceException("Meta should not null");
        }
        if (StringUtils.isNotBlank(book.getImage())) {
            book.setImage(fileStorage.loadFileToStorage(book.getImage()));
        }
        bookDataCenter.save(book);
        meta.getBooks().add(book);
        bookMetaDataCenter.refresh(meta);
    }

    @Override
    public void updateBook(Book book) {
        Book updated = bookDataCenter.findById(book.getId());
        updated.setStorage(book.getStorage());
        updated.setNote(book.getNote());
        updated.setCondition(book.getCondition());
        updated.setPosition(book.getPosition());
        if (StringUtils.isNotBlank(book.getImage())) {
            String oldImage = updated.getImage();
            updated.setImage(fileStorage.loadFileToStorage(book.getImage()));
            WorkerUtils.runAsync(() -> fileStorage.deleteFileFromStorage(oldImage));
        }
        bookDataCenter.update(updated);
    }

    @Override
    public void deleteBook(Book book) {
        book.setDeleted(true);
        bookDataCenter.delete(book);

        BookMeta meta = book.getMeta();
        if (meta != null) {
            meta.getBooks().remove(book);
            bookMetaDataCenter.refresh(meta);
        }
        Set<Ticket> tickets = book.getTickets();
        if (tickets.isEmpty()) {
            return;
        }
        Ticket borrowingTicket = book.getBorrowingTicket();
        if (borrowingTicket.getBooksCount() == 0) {
            borrowingTicket.setReturned(true);
            ticketDataCenter.update(borrowingTicket);
        }
        ticketDataCenter.refreshAll(tickets);
        Set<Reader> readers = tickets.stream().map(Ticket::getBorrower).collect(Collectors.toSet());
        readerDataCenter.refreshAll(readers);
    }

    @Override
    public List<Book> findAllNotBorrowedBookByIdIn(Collection<Integer> ids) {
        List<Book> books = bookRepo.findAllByIdIn(ids);
        return books.stream().filter(Book::notBorrowed).collect(Collectors.toList());
    }
}
