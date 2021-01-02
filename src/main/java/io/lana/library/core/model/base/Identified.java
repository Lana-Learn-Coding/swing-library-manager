package io.lana.library.core.model.base;

public interface Identified<ID> {
    ID getId();

    void setId(ID id);

    default String getIdString() {
        return isIdentified() ? getId().toString() : "";
    }

    default boolean isIdentified() {
        return getId() != null;
    }
}
