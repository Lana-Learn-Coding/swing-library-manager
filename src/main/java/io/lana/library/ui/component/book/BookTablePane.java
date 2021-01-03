package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.AbstractTablePane;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class BookTablePane extends AbstractTablePane<Book> {
    @Override
    protected Map<String, Function<Book, Object>> getColumnExtractorMapping() {
        Map<String, Function<Book, Object>> mapping = new LinkedHashMap<>(5);
        mapping.put("Storage", Book::getStorageName);
        mapping.put("Condition", Book::getCondition);
        mapping.put("Borrowed", Book::getBorrower);
        mapping.put("Borrowed Since", Book::getBorrowedDate);
        mapping.put("Due Date", Book::getDueDate);
        return mapping;
    }
}
