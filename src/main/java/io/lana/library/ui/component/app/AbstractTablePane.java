package io.lana.library.ui.component.app;

import io.lana.library.core.model.base.Identified;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractTablePane<T extends Identified<?>> extends JScrollPane {
    protected final Map<String, Function<T, Object>> columnsExtractorMapping = new LinkedHashMap<>();

    protected final List<T> data = new ArrayList<>();
    protected final JTable table;
    protected final DefaultTableModel tableModel;

    public AbstractTablePane() {
        columnsExtractorMapping.put("ID", Identified::getId);
        getColumnExtractorMapping().forEach(columnsExtractorMapping::putIfAbsent);
        tableModel = new DefaultTableModel(columnsExtractorMapping.keySet().toArray(), 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
        table.setAutoCreateRowSorter(true);
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
        T model = getRow(index);
        if (model != null) {
            data.remove(getRow(index));
        }
        tableModel.removeRow(index);
        tableModel.fireTableDataChanged();
    }

    public void removeSelectedRow() {
        removeRow(table.getSelectedRow());
    }

    public T getRow(int index) {
        Object id = table.getValueAt(index, 0);
        for (T model : data) {
            if (model.getId().equals(id)) {
                return model;
            }
        }
        return null;
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
        data.add(rowData);
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
