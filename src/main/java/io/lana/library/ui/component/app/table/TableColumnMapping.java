package io.lana.library.ui.component.app.table;

import io.lana.library.core.model.base.Identified;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableColumnMapping<T extends Identified<?>> {
    private final Map<String, ColumnMeta> columnMetaMap = new LinkedHashMap<>();

    private Class<?> defaultType = Object.class;

    @Getter
    private class ColumnMeta {
        private final Class<?> type;

        private final Function<T, ?> mapper;

        private ColumnMeta(Function<T, ?> mapper, Class<?> type) {
            this.mapper = mapper;
            this.type = type;
        }
    }

    public TableColumnMapping(String idColumn, Class<?> idType) {
        columnMetaMap.put(idColumn, new ColumnMeta(Identified::getId, idType));
    }

    public void put(String column, Function<T, ?> mapper) {
        columnMetaMap.putIfAbsent(column, new ColumnMeta(mapper, defaultType));
    }

    public void put(String column, Function<T, ?> mapper, Class<?> type) {
        columnMetaMap.putIfAbsent(column, new ColumnMeta(mapper, type));
    }

    public Class<?> getColumnType(String column) {
        if (!columnMetaMap.containsKey(column)) {
            return null;
        }
        return columnMetaMap.get(column).getType();
    }

    public Function<T, ?> getColumnMapper(String column) {
        if (!columnMetaMap.containsKey(column)) {
            return null;
        }
        return columnMetaMap.get(column).getMapper();
    }

    public Set<String> columnNames() {
        return columnMetaMap.keySet();
    }

    public Set<Function<T, ?>> columnMappers() {
        return columnMetaMap.values()
            .stream()
            .map(ColumnMeta::getMapper)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setDefaultColumnType(Class<?> defaultType) {
        this.defaultType = defaultType;
    }
}
