package io.lana.library.ui.component.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractTablePane<T> extends JScrollPane {
    protected final Map<String, Function<T, Object>> columnsExtractorMapping;

    protected final List<T> data = new ArrayList<>();
    protected final JTable table;
    protected final DefaultTableModel tableModel;

    public AbstractTablePane() {
        columnsExtractorMapping = getColumnExtractorMapping();
        if (columnsExtractorMapping == null) {
            throw new RuntimeException("Column mapping is null");
        }

        tableModel = new DefaultTableModel(columnsExtractorMapping.keySet().toArray(), 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
        setViewportView(table);
    }

    protected abstract Map<String, Function<T, Object>> getColumnExtractorMapping();

    protected Vector<Object> toTableRow(T model) {
        Vector<Object> data = new Vector<>();
        columnsExtractorMapping.values().forEach(mapper -> data.add(mapper.apply(model)));
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
        data.remove(index);
        tableModel.removeRow(index);
        tableModel.fireTableDataChanged();
    }

    public void removeSelectedRow() {
        removeRow(table.getSelectedRow());
    }

    public T getRow(int index) {
        return data.get(index);
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
        data.add(index, rowData);
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
}
