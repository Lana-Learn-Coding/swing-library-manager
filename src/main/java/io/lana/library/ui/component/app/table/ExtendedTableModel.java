package io.lana.library.ui.component.app.table;

import javax.swing.table.DefaultTableModel;

public class ExtendedTableModel extends DefaultTableModel {
    private final TableColumnMapping<?> tableColumnMapping;
    public ExtendedTableModel(TableColumnMapping<?> tableColumnMapping) {
        super(tableColumnMapping.columnNames().toArray(), 0);
        this.tableColumnMapping = tableColumnMapping;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tableColumnMapping.getColumnType(getColumnName(columnIndex));
    }
}
