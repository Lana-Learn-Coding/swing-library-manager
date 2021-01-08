package io.lana.library.ui.component;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReaderBorrowedBookTablePane extends AbstractListBasedTablePane<Book> {
    public ReaderBorrowedBookTablePane() {
        super();
        tableRowSorter.toggleSortOrder(6);
    }

    @Override
    protected TableColumnMapping<Book> getTableColumnMapping() {
        TableColumnMapping<Book> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Book::getId, Integer.class);
        mapping.put("Meta ID", (book) -> book.getMeta().getId(), Integer.class);
        mapping.put("Title", (book) -> book.getMeta().getTitle());
        mapping.put("Ticket", (book) -> "Ticket " + book.getBorrowing().getId());
        mapping.put("Overdue", (book) -> {
            if (LocalDate.now().isBefore(book.getDueDate())) {
                return "";
            }
            return "Overdue";
        });
        mapping.put("Overdue Days", (book) -> {
            if (LocalDate.now().isBefore(book.getDueDate())) {
                return 0;
            }
            return ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
        }, Integer.class);
        mapping.put("Borrowed Since", Book::getBorrowedDate, LocalDate.class);
        mapping.put("Due Date", Book::getDueDate, LocalDate.class);
        return mapping;
    }
}
