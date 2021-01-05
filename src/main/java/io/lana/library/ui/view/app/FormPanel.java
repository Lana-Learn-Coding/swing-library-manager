package io.lana.library.ui.view.app;

public interface FormPanel<T> {
    void clearForm();

    void loadModelToForm(T model);

    T getModelFromForm();
}
