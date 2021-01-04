package io.lana.library.ui.view.app;

public interface Form<T> {
    void clearForm();

    void loadModelToForm(T model);

    T getModelFromForm();
}
