package io.lana.library.ui.component;

import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.ui.component.app.table.AbstractRepoBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookMetaTablePane extends AbstractRepoBasedTablePane<BookMeta> {
    @Override
    protected TableColumnMapping<BookMeta> getTableColumnMapping() {
        TableColumnMapping<BookMeta> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", BookMeta::getId, Integer.class);
        mapping.put("Title", BookMeta::getTitle);
        mapping.put("Series", BookMeta::getSeriesName);
        mapping.put("Author", BookMeta::getAuthorName);
        mapping.put("Publisher", BookMeta::getPublisherName);
        mapping.put("Category", BookMeta::getCategoryName);
        mapping.put("Year", BookMeta::getYear, Integer.class);
        mapping.put("Books Count", bookMeta -> bookMeta.getBooks().size(), Integer.class);
        mapping.put("Borrowed Times",
            bookMeta -> bookMeta.getBooks().stream().mapToInt(Book::getBorrowedTimes).sum(),
            Integer.class);
        mapping.put("Created At", BaseEntity::getCreatedAt, LocalDateTime.class);
        return mapping;
    }
}
