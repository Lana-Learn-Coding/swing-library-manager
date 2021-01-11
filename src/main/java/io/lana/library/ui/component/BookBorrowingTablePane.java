package io.lana.library.ui.component;

import io.lana.library.core.model.book.Ticket;
import io.lana.library.ui.component.app.table.AbstractRepoBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BookBorrowingTablePane extends AbstractRepoBasedTablePane<Ticket> {
    @Override
    protected TableColumnMapping<Ticket> getTableColumnMapping() {
        TableColumnMapping<Ticket> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Ticket::getId, Integer.class);
        mapping.put("Borrower", ticket -> ticket.getBorrower().getId() + " - " + ticket.getBorrower().getName(), Integer.class);
        mapping.put("Book Count", ticket -> ticket.getBooks().size(), Integer.class);
        mapping.put("Overdue", ticket -> LocalDate.now().isBefore(ticket.getDueDate()) ? "" : "Overdue");
        mapping.put("Overdue Days", ticket -> LocalDate.now().isBefore(ticket.getDueDate()) ? 0 : ChronoUnit.DAYS.between(ticket.getDueDate(), LocalDate.now()), Integer.class);
        mapping.put("Borrowed Since", Ticket::getBorrowedDate, LocalDate.class);
        mapping.put("Due Date", Ticket::getDueDate, LocalDate.class);
        mapping.put("Created At", Ticket::getCreatedAt, LocalDateTime.class);
        return mapping;
    }
}
