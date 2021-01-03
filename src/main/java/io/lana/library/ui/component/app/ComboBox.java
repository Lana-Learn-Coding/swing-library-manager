package io.lana.library.ui.component.app;

import io.lana.library.core.model.base.Identified;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ComboBox<T extends Identified<?>> extends JComboBox<T> implements Iterable<T> {
    public List<T> getItemModels() {
        List<T> items = new ArrayList<>();
        forEach(items::add);
        return items;
    }

    public T getItem(Predicate<T> predicate) {
        for (T item : this) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    public T getSelectedItemModel() {
        return (T) super.getSelectedItem();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < getItemCount();
            }

            @Override
            public T next() {
                return getItemAt(index++);
            }
        };
    }

    public void removeItemModel(T item) {
        if (item == null) {
            super.removeItem(null);
            return;
        }
        super.removeItem(getItemModelById(item.getId()));
    }

    public void setSelectedItemModel(T item) {
        if (item == null) {
            super.setSelectedItem(null);
            return;
        }
        super.setSelectedItem(getItemModelById(item.getId()));
    }

    private T getItemModelById(Object id) {
        return getItem(item -> item.getId().equals(id));
    }
}
