package io.lana.library.ui.component;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReaderBorrowHistoryTablePane extends AbstractListBasedTablePane<BookMeta> {
    private Reader reader;

    public void setTableData(Reader reader) {
        this.reader = reader;
        super.setTableData(reader.getHistoryBorrowedBookMetas());
    }

    @Override
    protected TableColumnMapping<BookMeta> getTableColumnMapping() {
        TableColumnMapping<BookMeta> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", BookMeta::getId, Integer.class);
        mapping.put("Title", BookMeta::getTitle);
        mapping.put("Series", BookMeta::getSeriesName);
        mapping.put("Author", BookMeta::getAuthorName);
        mapping.put("Category", BookMeta::getCategoryName);
        mapping.put("Borrowed Times",
            bookMeta -> (int) bookMeta.getBooks().stream()
                .flatMap(book -> book.getTickets().stream())
                .filter(ticket -> ticket.getBorrower().equals(reader))
                .count(),
            Integer.class);
        mapping.put("Last Borrow", this::getLastBorrowedDate, LocalDate.class);
        mapping.put("Last Return", this::getLastReturnDate, LocalDate.class);
        return mapping;
    }

    private LocalDate getLastBorrowedDate(BookMeta bookMeta) {
        return getLastTicketDate(bookMeta, Ticket::getBorrowedDate);
    }

    private LocalDate getLastReturnDate(BookMeta bookMeta) {
        return getLastTicketDate(bookMeta, Ticket::getReturnedDate);
    }

    private LocalDate getLastTicketDate(BookMeta bookMeta, Function<Ticket, LocalDate> dateExtractor) {
        List<LocalDate> dates = bookMeta.getBooks().stream()
            .flatMap(book -> book.getTickets().stream())
            .filter(ticket -> ticket.getBorrower().equals(reader))
            .map(dateExtractor)
            .collect(Collectors.toList());
        if (dates.isEmpty() || dates.contains(null)) {
            return null;
        }
        dates.sort(Collections.reverseOrder());
        return dates.get(0);
    }
}
