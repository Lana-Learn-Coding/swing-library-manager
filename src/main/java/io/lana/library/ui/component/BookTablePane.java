package io.lana.library.ui.component;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

public class BookTablePane extends AbstractListBasedTablePane<Book> {
    @Override
    protected TableColumnMapping<Book> getTableColumnMapping() {
        TableColumnMapping<Book> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Book::getId, Integer.class);
        mapping.put("Title", Book::getTitle, Integer.class);
        mapping.put("Storage", Book::getStorageName);
        mapping.put("Borrower", Book::getBorrowerName);
        mapping.put("Borrowed Times", Book::getBorrowedTimes);
        mapping.put("Created At", Book::getCreatedAt);
        return mapping;
    }
}
