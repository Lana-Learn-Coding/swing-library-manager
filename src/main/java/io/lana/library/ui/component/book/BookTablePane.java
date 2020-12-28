package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookBorrowing;
import io.lana.library.ui.component.app.AbstractTablePane;
import io.lana.library.utils.DateFormatUtils;

import java.util.Vector;
import java.util.stream.Stream;

public class BookTablePane extends AbstractTablePane<Book> {
    @Override
    protected String[] getTableColumns() {
        return new String[]{"ID", "Storage", "Condition", "Borrowed", "Borrowed Since", "Due Date"};
    }

    @Override
    protected Vector<Object> toTableRow(Book model) {
        Vector<Object> data = new Vector<>();
        data.add(model.getId());
        data.add(model.getStorage().getName());
        data.add(model.getCondition() + "/10");
        if (model.getBorrowing() != null) {
            BookBorrowing borrowing = model.getBorrowing();
            data.add(borrowing.getBorrower().getEmail());
            data.add(DateFormatUtils.toDateStringWithDefaultUnknown(borrowing.getBorrowedDate()));
            data.add(DateFormatUtils.toDateStringWithDefaultUnknown(borrowing.getDueDate()));
        } else {
            Stream.iterate(0, i -> i + 1).limit(3)
                .forEach(i -> data.add("None"));

        }
        return data;
    }
}
