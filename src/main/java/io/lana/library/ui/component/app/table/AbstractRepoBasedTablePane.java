package io.lana.library.ui.component.app.table;

import io.lana.library.core.datacenter.base.RepositoryDataCenter;
import io.lana.library.core.model.base.Identified;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractRepoBasedTablePane<T extends Identified<?>> extends AbstractTablePane<T> implements Iterable<T> {
    protected RepositoryDataCenter<?, T> dataCenter;

    public void setRepositoryDataCenter(RepositoryDataCenter<?, T> dataCenter) {
        this.dataCenter = dataCenter;
        dataCenter.onSaved().subscribe(entity -> {
            tableModel.addRow(toTableRow(entity));
            int index = dataCenter.count() - 1;
            setSelectedRow(table.convertRowIndexToView(index));
        });
        dataCenter.onUpdate().subscribe(change -> {
            int row = dataCenter.indexOf(change.getNewValue());
            if (isAnyRowSelected()) {
                int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
                refreshRow(row);
                setSelectedRow(table.convertRowIndexToView(selectedRow));
                return;
            }
            refreshRow(row);
        });
        dataCenter.onDelete().subscribe(entity -> {
            int row = dataCenter.indexOf(entity);
            if (isAnyRowSelected()) {
                int selectedRow = table.getSelectedRow();
                tableModel.removeRow(row);
                setSelectedRow(selectedRow);
                return;
            }
            tableModel.removeRow(row);
        });
        refresh();
    }

    public void refresh() {
        setLoading(true);
        tableModel.setRowCount(0);
        dataCenter.findAll().forEach(model -> tableModel.addRow(toTableRow(model)));
        tableModel.fireTableDataChanged();
        clearFilter();
        setLoading(false);
    }

    public T getRow(int index) {
        int row = table.convertRowIndexToModel(index);
        return dataCenter.findByIndex(row);
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

    private void refreshRow(int row) {
        tableModel.removeRow(row);
        tableModel.insertRow(row, toTableRow(dataCenter.findByIndex(row)));
        tableModel.fireTableRowsUpdated(row, row);
    }

    public List<T> asList() {
        return dataCenter.findAll();
    }

    @Override
    public Iterator<T> iterator() {
        return dataCenter.iterator();
    }

    public int indexOf(T row) {
        return table.convertRowIndexToView(dataCenter.indexOf(row));
    }

    public int rowCount() {
        return dataCenter.count();
    }

    public Stream<T> stream() {
        return dataCenter.stream();
    }
}