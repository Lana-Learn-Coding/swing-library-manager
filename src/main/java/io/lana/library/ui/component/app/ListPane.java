package io.lana.library.ui.component.app;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collection;

public class ListPane<T> extends JPanel {
    private final JScrollPane scrollPane = new JScrollPane();
    private final JList<T> list = new JList<>();
    private final DefaultListModel<T> listModel = new DefaultListModel<>();

    public ListPane() {
        initComponents();
        list.setModel(listModel);
    }

    private void initComponents() {
        setLayout(new GridLayout());
        scrollPane.setViewportView(list);
        add(scrollPane);
    }

    public void setListData(Collection<T> data) {
        listModel.removeAllElements();
        data.forEach(listModel::addElement);
    }

    public void clearListData() {
        listModel.removeAllElements();
    }

    public T getSelectedValue() {
        return list.getSelectedValue();
    }

    public boolean isAnySelected() {
        return list.getSelectedIndex() >= 0;
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }
}
