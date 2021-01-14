package io.lana.library.core.service;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.spi.datacenter.BookDataCenter;
import io.lana.library.core.spi.datacenter.ReaderDataCenter;
import io.lana.library.core.spi.datacenter.TicketDataCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Component
public class TicketServiceImpl implements TicketService {
    private BookDataCenter bookDataCenter;

    private ReaderDataCenter readerDataCenter;

    private TicketDataCenter ticketDataCenter;

    @Autowired
    public void setup(BookDataCenter bookDataCenter, ReaderDataCenter readerDataCenter, TicketDataCenter ticketDataCenter) {
        this.bookDataCenter = bookDataCenter;
        this.readerDataCenter = readerDataCenter;
        this.ticketDataCenter = ticketDataCenter;
    }

    @Override
    public void createTicket(Ticket ticket) {
        ticketDataCenter.save(ticket);
        Collection<Book> books = ticket.getBooks();
        books.forEach(book -> book.getTickets().add(ticket));
        bookDataCenter.refreshAll(books);

        Reader reader = ticket.getBorrower();
        reader.getTickets().add(ticket);
        readerDataCenter.refresh(reader);
    }

    @Override
    public Ticket returnBook(Book book) {
        Ticket ticket = book.getBorrowingTicket();
        if (ticket.getBooksCount() <= 1) {
            returnTicket(ticket);
            return ticket;
        }
        ticket.getBooks().remove(book);
        book.getTickets().remove(ticket);
        ticketDataCenter.update(ticket);

        Ticket newTicket = ticket.withBook(book);
        newTicket.setId(null);
        newTicket.setReturned(true);
        ticketDataCenter.save(newTicket);
        book.getTickets().add(newTicket);

        bookDataCenter.refresh(book);
        ticket.getBorrower().getTickets().add(newTicket);
        readerDataCenter.refresh(ticket.getBorrower());
        return newTicket;
    }

    @Override
    public void returnTicket(Ticket ticket) {
        ticket.setReturned(true);
        ticketDataCenter.update(ticket);
        ticket.getBooks().forEach(book -> bookDataCenter.refresh(book));
        readerDataCenter.refresh(ticket.getBorrower());
    }

    @Override
    public Ticket extendBookDueDate(Book book, LocalDate newDueDate) {
        Ticket ticket = book.getBorrowingTicket();
        if (ticket.getBooksCount() <= 1) {
            extendTicketDueDate(ticket, newDueDate);
            return ticket;
        }
        ticket.getBooks().remove(book);
        book.getTickets().remove(ticket);
        ticketDataCenter.update(ticket);

        Ticket newTicket = ticket.withBook(book);
        newTicket.setId(null);
        newTicket.setDueDate(newDueDate);
        ticketDataCenter.save(newTicket);
        book.getTickets().add(newTicket);

        ticket.getBorrower().getTickets().add(newTicket);
        readerDataCenter.refresh(ticket.getBorrower());
        return ticket;
    }

    @Override
    public void extendTicketDueDate(Ticket ticket, LocalDate newDueDate) {
        if (ticket.isReturned()) {
            throw new ServiceException("Cannot extend due date of ticket has returned");
        }
        if (newDueDate.isBefore(LocalDate.now())) {
            throw new ServiceException("New due date must not before now");
        }
        ticket.setDueDate(newDueDate);
        ticketDataCenter.update(ticket);
        bookDataCenter.refreshAll(ticket.getBooks());
    }

    @Override
    public void updateTicket(Ticket ticket) {
        Ticket updated = ticketDataCenter.findById(ticket.getId());

        boolean dueDateChanged = !ticket.getDueDate().equals(updated.getDueDate());
        boolean returnChanged = ticket.isReturned() != updated.isReturned();

        updated.setNote(ticket.getNote());
        updated.setDueDate(ticket.getDueDate());
        updated.setReturned(ticket.isReturned());
        ticketDataCenter.update(updated);

        if (dueDateChanged || returnChanged) {
            bookDataCenter.refreshAll(updated.getBooks());
            readerDataCenter.refresh(updated.getBorrower());
        }
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        ticketDataCenter.delete(ticket);

        ticket.getBorrower().getTickets().remove(ticket);
        readerDataCenter.refresh(ticket.getBorrower());

        Set<Book> books = ticket.getBooks();
        books.forEach(book -> book.getTickets().remove(ticket));
        bookDataCenter.refreshAll(books);
    }
}
