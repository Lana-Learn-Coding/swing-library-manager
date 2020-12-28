package io.lana.library.ui.component.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public abstract class AbstractTablePane<T> extends JScrollPane {
    protected final List<T> data = new ArrayList<>();
    protected final JTable table = new JTable();
    private final DefaultTableModel tableModel;

    public AbstractTablePane() {
        tableModel = new DefaultTableModel(getTableColumns(), 0);
        table.setModel(tableModel);
        setViewportView(table);
    }

    protected abstract String[] getTableColumns();

    protected abstract Vector<Object> toTableRow(T model);

    public JTable getTable() {
        return table;
    }

    public void setTableData(Collection<T> data) {
        this.data.clear();
        this.data.addAll(data);
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
        return getRow(table.getSelectedRow());
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

    public ListSelectionModel getSelectionModel() {
        return table.getSelectionModel();
    }

    public void setSelectedRow(int index) {
        table.setRowSelectionInterval(index, index);
    }
}
