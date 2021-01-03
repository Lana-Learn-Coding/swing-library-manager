package io.lana.library.ui.component.app.table;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public abstract class AbstractTablePane<T> extends JPanel {
    protected final List<T> data = new ArrayList<>();

    protected final JTextField search = new JTextField();
    protected final JScrollPane scrollPane = new JScrollPane();
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
        JButton btnSearch = new JButton();
        btnSearch.setText("Search");

        search.addActionListener(e -> onSearch());
        btnSearch.addActionListener(e -> onSearch());

        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 1.0, 1.0E-4};

        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 1.0, 1.0E-4};

        add(search, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 8, 5), 0, 0));
        add(btnSearch, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 8, 0), 0, 0));
        add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
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

    public void setTableData(Collection<T> data) {
        this.data.clear();
        this.data.addAll(data);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        data.forEach(model -> tableModel.addRow(toTableRow(model)));
        tableModel.fireTableDataChanged();
    }

    public void removeRow(int index) {
        int row = table.convertRowIndexToModel(index);
        data.remove(row);
        tableModel.removeRow(row);
        tableModel.fireTableDataChanged();
    }

    public void removeSelectedRow() {
        removeRow(table.getSelectedRow());
    }

    public T getRow(int index) {
        int row = table.convertRowIndexToModel(index);
        return data.get(row);
    }

    public T getSelectedRow() {
        if (isAnyRowSelected()) {
            return getRow(table.getSelectedRow());
        }
        return null;
    }

    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void addRow(T rowData) {
        data.add(rowData);
        tableModel.addRow(toTableRow(rowData));
        tableModel.fireTableDataChanged();
    }

    public void addRow(int index, T rowData) {
        int row = table.convertRowIndexToModel(index);
        data.add(row, rowData);
        tableModel.insertRow(index, toTableRow(rowData));
        tableModel.fireTableDataChanged();
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
    }

    public void refreshSelectedRow() {
        if (isAnyRowSelected()) {
            fireTableRowsUpdated(table.getSelectedRow());
        }
    }

    protected void onSearch() {
        String text = search.getText();
        if (StringUtils.isBlank(text)) {
            clearSearch();
            return;
        }
        tableRowSorter.setRowFilter(RowFilter.regexFilter(text));
        clearSelection();
    }

    public void clearSearch() {
        search.setText("");
        tableRowSorter.setRowFilter(null);
        clearSelection();
    }

    public void fireTableRowsUpdated(int index) {
        int row = table.convertRowIndexToModel(index);
        tableModel.removeRow(row);
        tableModel.insertRow(row, toTableRow(data.get(row)));
        tableModel.fireTableRowsUpdated(row, row);
    }
}