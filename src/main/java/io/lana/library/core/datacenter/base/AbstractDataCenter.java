package io.lana.library.core.datacenter.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public abstract class AbstractDataCenter<T> implements DataCenter<T> {
    protected final Sinks.Many<T> savedSink = Sinks.many().multicast().directAllOrNothing();
    protected final Sinks.Many<T> saveSink = Sinks.many().multicast().directAllOrNothing();

    protected final Sinks.Many<DataChange<T>> updatedSink = Sinks.many().multicast().directAllOrNothing();
    protected final Sinks.Many<DataChange<T>> updateSink = Sinks.many().multicast().directAllOrNothing();

    protected final Sinks.Many<T> deletedSink = Sinks.many().multicast().directAllOrNothing();
    protected final Sinks.Many<T> deleteSink = Sinks.many().multicast().directAllOrNothing();

    @Override
    public Flux<T> onSave() {
        return savedSink.asFlux();
    }

    @Override
    public Flux<T> onSaved() {
        return saveSink.asFlux();
    }

    @Override
    public Flux<DataChange<T>> onUpdate() {
        return updateSink.asFlux();
    }

    @Override
    public Flux<DataChange<T>> onUpdated() {
        return updatedSink.asFlux();
    }

    @Override
    public Flux<T> onDelete() {
        return deleteSink.asFlux();
    }

    @Override
    public Flux<T> onDeleted() {
        return deletedSink.asFlux();
    }
}
