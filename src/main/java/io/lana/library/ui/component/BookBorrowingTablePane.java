package io.lana.library.ui.component;

import io.lana.library.core.model.book.BookBorrowing;
import io.lana.library.ui.component.app.table.AbstractTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BookBorrowingTablePane extends AbstractTablePane<BookBorrowing> {
    @Override
    protected TableColumnMapping<BookBorrowing> getTableColumnMapping() {
        TableColumnMapping<BookBorrowing> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", BookBorrowing::getId, Integer.class);
        mapping.put("Book Count", ticket -> ticket.getBooks().size(), Integer.class);
        mapping.put("Overdue", ticket -> LocalDate.now().isBefore(ticket.getDueDate()) ? "" : "Overdue");
        mapping.put("Overdue Days", ticket -> LocalDate.now().isBefore(ticket.getDueDate()) ? 0 : ChronoUnit.DAYS.between(ticket.getDueDate(), LocalDate.now()), Integer.class);
        mapping.put("Borrowed Since", BookBorrowing::getBorrowedDate, LocalDate.class);
        mapping.put("Due Date", BookBorrowing::getDueDate, LocalDate.class);
        mapping.put("Created At", BookBorrowing::getCreatedAt, LocalDateTime.class);
        return mapping;
    }
}
