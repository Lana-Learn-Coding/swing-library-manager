package io.lana.library.ui.component;

import io.lana.library.core.model.book.Book;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

public class BorrowBookTablePane extends AbstractListBasedTablePane<Book> {
    @Override
    protected TableColumnMapping<Book> getTableColumnMapping() {
        TableColumnMapping<Book> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Book::getId, Integer.class);
        mapping.put("Meta", (book) -> book.getMeta().getId(), Integer.class);
        mapping.put("Title", (book) -> book.getMeta().getTitle());
        mapping.put("Author", (book) -> book.getMeta().getAuthorName());
        mapping.put("Category", (book) -> book.getMeta().getCategoryName());
        mapping.put("Series", (book) -> book.getMeta().getSeriesName());
        return mapping;
    }
}
