package io.lana.library.ui.component;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;

public class BookTablePane extends AbstractListBasedTablePane<Book> {
    @Override
    protected TableColumnMapping<Book> getTableColumnMapping() {
        TableColumnMapping<Book> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Book::getId, Integer.class);
        mapping.put("Storage", Book::getStorageName);
        mapping.put("Condition", Book::getCondition);
        mapping.put("Borrower", Book::getBorrowerName);
        mapping.put("Borrower phone", Book::getBorrowerPhone);
        mapping.put("Borrowed Since", Book::getBorrowedDate, LocalDate.class);
        mapping.put("Due Date", Book::getDueDate, LocalDate.class);
        return mapping;
    }
}
