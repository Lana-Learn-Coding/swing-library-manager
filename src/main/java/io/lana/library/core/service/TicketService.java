package io.lana.library.core.service;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;

import java.time.LocalDate;

public interface TicketService {
    void createTicket(Ticket ticket);

    void returnBook(Book book);

    void returnTicket(Ticket ticket);

    Ticket extendBookDueDate(Book book, LocalDate newDueDate);

    void extendTicketDueDate(Ticket ticket, LocalDate newDueDate);

    void updateTicket(Ticket ticket);

    void deleteTicket(Ticket ticket);
}
