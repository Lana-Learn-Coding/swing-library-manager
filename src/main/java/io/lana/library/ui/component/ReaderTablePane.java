package io.lana.library.ui.component;

import io.lana.library.core.model.Reader;
import io.lana.library.ui.component.app.table.AbstractTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDateTime;

public class ReaderTablePane extends AbstractTablePane<Reader> {
    @Override
    protected TableColumnMapping<Reader> getTableColumnMapping() {
        TableColumnMapping<Reader> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", Reader::getId, Integer.class);
        mapping.put("Name", Reader::getName);
        mapping.put("Phone", Reader::getPhoneNumber);
        mapping.put("Email", Reader::getEmail);
        mapping.put("Gender", Reader::getGenderString);
        mapping.put("Limit", Reader::getLimit, Integer.class);
        mapping.put("Borrowed Book", Reader::getBorrowedBookCount, Integer.class);
        mapping.put("Created At", Reader::getCreatedAt, LocalDateTime.class);
        return mapping;
    }
}
