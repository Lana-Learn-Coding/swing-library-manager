package io.lana.library.ui.component.app.table;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

public abstract class AbstractTablePane<T> extends JPanel implements Iterable<T> {
    protected RowFilter<Object, Object> filter;

    protected final JTextField search = new JTextField();
    protected final JScrollPane scrollPane = new JScrollPane();
    protected final JProgressBar progressBar = new JProgressBar();
    protected final JButton btnSearch = new JButton();
    protected final JTable table;

    protected final TableColumnMapping<T> tableColumnMapping;
    protected final DefaultTableModel tableModel;
    protected final TableRowSorter<DefaultTableModel> tableRowSorter;
    protected final DefaultTableCellRenderer tableCellRenderer = new ExtendedTableCellRenderer();


    public AbstractTablePane() {
        tableColumnMapping = getTableColumnMapping();
        tableModel = new ExtendedTableModel(tableColumnMapping);
        tableRowSorter = new TableRowSorter<>(tableModel);

        table = new JTable();
        table.setModel(tableModel);
        table.setDefaultRenderer(Object.class, tableCellRenderer);
        table.setRowSorter(tableRowSorter);
        scrollPane.setViewportView(table);
        initComponents();
    }

    public void initComponents() {
        btnSearch.setText("Search");

        search.addActionListener(e -> onSearch());
        btnSearch.addActionListener(e -> onSearch());

        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};


        add(search, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 8, 5), 0, 0));
        add(btnSearch, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 8, 0), 0, 0));
        add(progressBar, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));
        add(scrollPane, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    }

    protected abstract TableColumnMapping<T> getTableColumnMapping();

    protected Vector<Object> toTableRow(T model) {
        Vector<Object> data = new Vector<>();
        tableColumnMapping.columnMappers().forEach(mapper -> data.add(mapper.apply(model)));
        return data;
    }

    public JTable getTable() {
        return table;
    }


    public void clearSelection() {
        table.clearSelection();
    }

    public boolean isAnyRowSelected() {
        return table.getSelectedRow() >= 0;
    }

    public ListSelectionModel getSelectionModel() {
        return table.getSelectionModel();
    }

    public void setSelectedRow(int index) {
        table.setRowSelectionInterval(index, index);
        scrollToSelected();
    }

    protected void onSearch() {
        applyFilter();
    }

    public void clearSearch() {
        search.setText("");
        applyFilter();
    }

    public void setFilter(RowFilter<Object, Object> filter) {
        this.filter = filter;
        search.setText("");
        applyFilter();
    }

    public void clearFilter() {
        setFilter(null);
    }

    protected void applyFilter() {
        List<RowFilter<Object, Object>> rowFilters = new ArrayList<>();
        if (filter != null) {
            rowFilters.add(filter);
        }

        if (StringUtils.isNotBlank(search.getText())) {
            rowFilters.add(RowFilter.regexFilter(search.getText()));
        }

        if (rowFilters.isEmpty()) {
            tableRowSorter.setRowFilter(null);
            return;
        }
        tableRowSorter.setRowFilter(RowFilter.andFilter(rowFilters));
        clearSelection();
    }

    public void setLoading(boolean isLoading) {
        search.setEnabled(!isLoading);
        table.setEnabled(!isLoading);
        btnSearch.setEnabled(!isLoading);
        progressBar.setIndeterminate(isLoading);
    }

    protected void scrollToSelected() {
        Rectangle cellRectangle = table.getCellRect(table.getSelectedRow(), 0, true);
        Rectangle visibleRectangle = scrollPane.getVisibleRect();
        SwingUtilities.invokeLater(() -> {
            table.scrollRectToVisible(new Rectangle(cellRectangle.x, cellRectangle.y, (int) visibleRectangle.getWidth(),
                (int) visibleRectangle.getHeight()));
        });
    }

    public abstract void refresh();

    public abstract List<T> asList();

    public abstract int indexOf(T row);

    public abstract int rowCount();

    public abstract Stream<T> stream();
}