package io.lana.library.ui.view.app;

public interface CrudPanel<T> extends FormPanel<T> {

    void delete();

    void save();
}
