package io.lana.library.core.datacenter.base;

import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.stream.Stream;

public interface DataCenter<T> extends Iterable<T> {
    Collection<T> findAll();

    Stream<T> stream();

    Flux<T> onSave();

    Flux<T> onSaved();

    Flux<DataChange<T>> onUpdate();

    Flux<DataChange<T>> onUpdated();

    Flux<T> onDelete();

    Flux<T> onDeleted();
}
