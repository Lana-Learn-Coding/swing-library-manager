package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.ui.component.app.AbstractTablePane;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static io.lana.library.utils.DateFormatUtils.COMMON_DATE_FORMAT;

@Component
public class BookMetaTablePane extends AbstractTablePane<BookMeta> {
    @Override
    protected Map<String, Function<BookMeta, Object>> getColumnExtractorMapping() {
        Map<String, Function<BookMeta, Object>> mapping = new LinkedHashMap<>(9);
        mapping.put("ID", BookMeta::getId);
        mapping.put("Title", BookMeta::getTitle);
        mapping.put("Series", BookMeta::getSeriesName);
        mapping.put("Author", BookMeta::getAuthorName);
        mapping.put("Publisher", BookMeta::getPublisherName);
        mapping.put("Category", BookMeta::getCategoryName);
        mapping.put("Year", BookMeta::getYear);
        mapping.put("Count", bookMeta -> bookMeta.getBooks().size());
        mapping.put("Updated", bookMeta -> bookMeta.getUpdatedAt().toLocalDate().format(COMMON_DATE_FORMAT));
        return mapping;
    }
}
