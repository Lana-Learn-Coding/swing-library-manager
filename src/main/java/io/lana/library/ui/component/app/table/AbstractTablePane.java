package io.lana.library.ui.component.app.table;

import io.lana.library.core.model.base.Identified;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;

public abstract class AbstractTablePane<T> extends JScrollPane {
    protected final TableColumnMapping<T> tableColumnMapping;

    protected final List<T> data = new ArrayList<>();
    protected final JTable table;
    protected final DefaultTableModel tableModel;
    protected final DefaultTableCellRenderer tableCellRenderer = new ExtendedTableCellRenderer();

    public AbstractTablePane() {
        tableColumnMapping = getTableColumnMapping();
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setModel(new ExtendedTableModel(tableColumnMapping));
        table.setDefaultRenderer(Object.class, tableCellRenderer);

        tableModel = (DefaultTableModel) table.getModel();
        setViewportView(table);
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
        data.remove(index);
        tableModel.removeRow(index);
        tableModel.fireTableDataChanged();
    }

    public void removeSelectedRow() {
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        removeRow(row);
    }

    public T getRow(int index) {
        int row = table.convertRowIndexToModel(index);
        return data.get(row);
    }

    public T getSelectedRow() {
        if (isAnyRowSelected()) {
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            return getRow(row);
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