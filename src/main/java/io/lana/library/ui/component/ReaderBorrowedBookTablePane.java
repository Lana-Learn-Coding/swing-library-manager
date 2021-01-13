package io.lana.library.ui.component;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;

public class ReaderBorrowedBookTablePane extends AbstractListBasedTablePane<Book> {
    @Override
    protected TableColumnMapping<Book> getTableColumnMapping() {
        TableColumnMapping<Book> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Book::getId, Integer.class);
        mapping.put("Meta ID", (book) -> book.getMeta().getId(), Integer.class);
        mapping.put("Title", (book) -> book.getMeta().getTitle());
        mapping.put("Ticket", (book) -> "Ticket " + book.getBorrowingTicket().getId());
        mapping.put("Overdue", (book) -> {
            Ticket ticket = book.getBorrowingTicket();
            if (ticket != null && ticket.isOverDue()) {
                return "Overdue";
            }
            return "";
        });
        mapping.put("Overdue Days", (book) -> {
            Ticket ticket = book.getBorrowingTicket();
            return ticket.getOverDueDays();
        }, Integer.class);
        mapping.put("Borrowed Since", Book::getBorrowedDate, LocalDate.class);
        mapping.put("Due Date", Book::getDueDate, LocalDate.class);
        return mapping;
    }
}
