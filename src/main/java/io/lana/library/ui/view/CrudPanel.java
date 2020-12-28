package io.lana.library.ui.view;

import java.util.Collection;

public interface CrudPanel<T> {

    void delete();

    void save();

    void clearForm();

    void loadModelToForm(T model);

    T getModelFromForm();

    void renderTable(Collection<T> data);

    void renderTable();
}
