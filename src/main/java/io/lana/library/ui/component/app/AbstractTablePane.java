package io.lana.library.ui.component.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public abstract class AbstractTablePane<T> extends JScrollPane {
    protected final JTable table = new JTable();

    public AbstractTablePane() {
        initComponents();
    }

    private void initComponents() {
        table.setModel(new DefaultTableModel(getTableColumns(), 0));
        setViewportView(table);
    }

    protected abstract String[] getTableColumns();

    protected abstract Vector<Object> toTableRow(T model);

    public JTable getTable() {
        return table;
    }

    public void setTableData(List<T> data) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        data.forEach(model -> tableModel.addRow(toTableRow(model)));
        tableModel.fireTableDataChanged();
    }
}
