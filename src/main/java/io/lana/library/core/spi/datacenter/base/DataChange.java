package io.lana.library.core.spi.datacenter.base;

import lombok.Getter;

@Getter
public class DataChange<T> {
    private final T oldValue;

    private final T newValue;

    private boolean changed = true;

    public DataChange(T oldValue, T newValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public static <T> DataChange<T> unchanged(T value) {
        DataChange<T> dataChange = new DataChange<>(value, value);
        dataChange.changed = false;
        return dataChange;
    }
}
