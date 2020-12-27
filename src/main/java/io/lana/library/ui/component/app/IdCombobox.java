package io.lana.library.ui.component.app;

import io.lana.library.core.model.base.Identified;

public class IdCombobox<T extends Identified<Integer>> extends ComboBox<T> {
    public void removeItem(T item) {
        if (item == null) {
            super.removeItem(null);
            return;
        }
        super.removeItem(getItemById(item.getId()));
    }

    public void setSelectedItem(T item) {
        if (item == null) {
            super.setSelectedItem(null);
            return;
        }
        super.setSelectedItem(getItemById(item.getId()));
    }

    private T getItemById(Integer id) {
        return getItem(item -> item.getId().equals(id));
    }
}
