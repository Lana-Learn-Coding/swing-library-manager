package io.lana.library.ui.component.app;

import javax.swing.*;
import java.util.Iterator;

public class ComboBox<T> extends JComboBox<T> implements Iterable<T> {

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
