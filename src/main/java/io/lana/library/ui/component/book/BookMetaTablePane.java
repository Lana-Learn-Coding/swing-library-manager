package io.lana.library.ui.component.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.repo.BookMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

@Component
@Scope("prototype")
public class BookMetaTablePane extends JScrollPane {
    private BookMetaRepo bookMetaRepo;

    private JTable table;

    @Autowired
    public void setBookMetaRepo(BookMetaRepo bookMetaRepo) {
        this.bookMetaRepo = bookMetaRepo;
        setTableData(bookMetaRepo.findAllByOrderByUpdatedAtDesc());
    }

    public BookMetaTablePane() {
        initContentTable();
    }

    private void initContentTable() {
        table = new JTable();
        String[] bookMetaColumns = new String[]{
            "ID", "Title", "Series", "Author", "Publisher", "Category", "Count", "Year", "Updated"
        };
        table.setModel(new DefaultTableModel(bookMetaColumns, 0));
        setViewportView(table);
    }

    public void setTableData(List<BookMeta> bookMetaList) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        bookMetaList.forEach(bookMeta -> {
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
            model.addRow(data);
        });
        model.fireTableDataChanged();
    }

    public void addBookMeta() {

    }
}
