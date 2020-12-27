package io.lana.library.ui.component.app;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ComboBox<T> extends JComboBox<T> implements Iterable<T> {
    public List<T> getItems() {
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
}
