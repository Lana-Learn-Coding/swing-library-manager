package io.lana.library.ui.component.app.table;

import io.lana.library.utils.DateFormatUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

class ExtendedTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof LocalDate || value instanceof LocalDateTime || value instanceof Date) {
            value = DateFormatUtils.toDateStringWithDefaultUnknown(value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
