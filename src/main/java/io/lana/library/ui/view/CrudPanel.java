package io.lana.library.ui.view;

import java.util.List;

public interface CrudPanel<T> {

    void delete();

    void save();

    void clearForm();

    void loadModelToForm(T model);

    T getModelFromForm();

    void renderTable(List<T> data);

    void renderTable();
}
