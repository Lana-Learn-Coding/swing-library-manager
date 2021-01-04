package io.lana.library.ui.component.app;

import io.lana.library.core.model.base.Identified;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ComboBox<T extends Identified<?>> extends JComboBox<T> implements Iterable<T> {
    private final Map<Object, T> itemModelsMap = new HashMap<>();

    public T getSelectedItem() {
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

    @Override
    public void addItem(T item) {
        super.addItem(item);
        itemModelsMap.put(item.getId(), item);
    }

    public void addAllItem(Iterable<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    @Override
    public void insertItemAt(T item, int index) {
        super.insertItemAt(item, index);
        itemModelsMap.put(item.getId(), item);
    }

    @Override
    public void removeItem(Object anObject) {
        if (!Identified.class.isAssignableFrom(anObject.getClass())) {
            super.removeItem(anObject);
            return;
        }

        Object key = ((Identified<?>) anObject).getId();
        if (key != null && itemModelsMap.containsKey(key)) {
            for (T item : this) {
                if (item.getId().equals(key)) {
                    super.removeItem(item);
                    break;
                }
            }
            itemModelsMap.remove(key);
        }
    }

    @Override
    public void removeItemAt(int anIndex) {
        super.removeItemAt(anIndex);
        T item = getItemAt(anIndex);
        itemModelsMap.remove(item.getId());
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        itemModelsMap.clear();
    }

    public void setSelectedItem(T item) {
        if (item == null) {
            super.setSelectedItem(null);
            return;
        }
        super.setSelectedItem(getItemModelById(item.getId()));
    }

    private T getItemModelById(Object id) {
        if (id == null) {
            return null;
        }
        return itemModelsMap.get(id);
    }

    public void refreshModel() {
        itemModelsMap.clear();
        for (int i = 0; i < getModel().getSize(); i++) {
            T item = getModel().getElementAt(i);
            itemModelsMap.put(item.getId(), item);
        }
    }
}
