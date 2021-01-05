package io.lana.library.ui.view.app;

import java.util.Collection;

public interface CrudPanel<T> extends FormPanel<T> {

    void delete();

    void save();

    void renderTable(Collection<T> data);

    void renderTable();
}
