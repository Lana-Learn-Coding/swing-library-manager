package io.lana.library.ui.component.app.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractListBasedTablePane<T> extends AbstractTablePane<T> implements Iterable<T> {
    protected final List<T> data = new ArrayList<>();

    public void setTableData(Collection<T> data) {
        this.data.clear();
        this.data.addAll(data);
        refresh();
    }

    public void refresh() {
        setLoading(true);
        tableModel.setRowCount(0);
        data.forEach(model -> tableModel.addRow(toTableRow(model)));
        tableModel.fireTableDataChanged();
        clearFilter();
        setLoading(false);
    }

    public void removeRow(int index) {
        int row = table.convertRowIndexToModel(index);
        data.remove(row);
        tableModel.removeRow(row);
    }

    public void removeRow(T row) {
        int index = data.indexOf(row);
        data.remove(index);
        tableModel.removeRow(index);
    }

    public void removeSelectedRow() {
        removeRow(table.getSelectedRow());
        clearSelection();
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
    }

    public void addRow(int index, T rowData) {
        int row = table.convertRowIndexToModel(index);
        data.add(row, rowData);
        tableModel.insertRow(index, toTableRow(rowData));
    }

    public void refreshSelectedRow() {
        if (isAnyRowSelected()) {
            int selectedRow = table.getSelectedRow();
            refreshRow(selectedRow);
            setSelectedRow(selectedRow);
        }
    }

    public void refreshRow(int index) {
        int row = table.convertRowIndexToModel(index);
        tableModel.removeRow(row);
        tableModel.insertRow(row, toTableRow(data.get(row)));
        tableModel.fireTableRowsUpdated(row, row);
    }

    public void refreshRow(T row) {
        int index = data.indexOf(row);
        tableModel.removeRow(index);
        tableModel.insertRow(index, toTableRow(row));
        tableModel.fireTableRowsUpdated(index, index);
    }

    public List<T> asList() {
        return new ArrayList<>(data);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    public int indexOf(T row) {
        return table.convertRowIndexToView(data.indexOf(row));
    }

    public int rowCount() {
        return data.size();
    }

    public Stream<T> stream() {
        return data.stream();
    }
}