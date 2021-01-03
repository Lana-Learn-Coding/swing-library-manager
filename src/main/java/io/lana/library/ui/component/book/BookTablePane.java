package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.AbstractTablePane;
import io.lana.library.utils.DateFormatUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class BookTablePane extends AbstractTablePane<Book> {
    @Override
    protected Map<String, Function<Book, Object>> getColumnExtractorMapping() {
        Map<String, Function<Book, Object>> mapping = new LinkedHashMap<>(6);
        mapping.put("ID", Book::getId);
        mapping.put("Storage", Book::getStorageName);
        mapping.put("Condition", book -> book.getCondition().toString() + "/10");
        mapping.put("Borrowed", Book::getBorrower);
        mapping.put("Borrowed Since", book -> book.isBorrowed() ?
            DateFormatUtils.toDateStringWithDefaultUnknown(book.getBorrowedDate()) : "");
        mapping.put("Due Date", book -> book.isBorrowed() ?
            DateFormatUtils.toDateStringWithDefaultUnknown(book.getDueDate()) : "");
        return mapping;
    }
}
