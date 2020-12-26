package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.ui.component.app.AbstractTablePane;
import org.springframework.stereotype.Component;

import java.util.Vector;

@Component
public class BookMetaTablePane extends AbstractTablePane<BookMeta> {
    @Override
    protected String[] getTableColumns() {
        return new String[]{"ID", "Title", "Series", "Author", "Publisher", "Category", "Count", "Year", "Updated"};
    }

    @Override
    protected Vector<Object> toTableRow(BookMeta bookMeta) {
        Vector<Object> data = new Vector<>();
        data.add(bookMeta.getId());
        data.add(bookMeta.getTitle());
        data.add(bookMeta.getSeriesName());
        data.add(bookMeta.getAuthorName());
        data.add(bookMeta.getPublisherName());
        data.add(bookMeta.getCategoryName());
        data.add(bookMeta.getBooks().size());
        data.add(bookMeta.getYear());
        data.add(bookMeta.getUpdatedAt().toLocalDate().toString());
        return data;
    }
}
