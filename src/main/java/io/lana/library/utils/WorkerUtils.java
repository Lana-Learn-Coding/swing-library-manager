package io.lana.library.utils;

import javax.swing.*;
import java.util.concurrent.*;

public class WorkerUtils {
    // Executor for loop checking Future status
    // Used for convert Future -> CompletableFuture
    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();

    private static void schedule(Runnable r) {
        SERVICE.schedule(r, 1, TimeUnit.MILLISECONDS);
    }

    // Immediate class for converting Future -> CompletableFuture
    private static class CompletablePromise<V> extends CompletableFuture<V> {
        private final Future<V> future;

        public CompletablePromise(Future<V> future) {
            this.future = future;
            schedule(this::tryToComplete);
        }

        private void tryToComplete() {
            if (future.isDone()) {
                try {
                    complete(future.get());
                } catch (InterruptedException e) {
                    completeExceptionally(e);
                } catch (ExecutionException e) {
                    completeExceptionally(e.getCause());
                }
                return;
            }

            if (future.isCancelled()) {
                cancel(true);
                return;
            }

            schedule(this::tryToComplete);
        }
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                runnable.run();
                completableFuture.complete(null);
                return null;
            }
        };
        worker.execute();
        return completableFuture;
    }

    public static <T> CompletableFuture<T> supplyAsync(Callable<T> callable) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    T result = callable.call();
                    completableFuture.complete(result);
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
                return null;
            }
        };
        worker.execute();
        return completableFuture;
    }

    public static <T> CompletableFuture<T> supplyAsync(SwingWorker<T, ?> worker) {
        worker.execute();
        return new CompletablePromise<>(worker);
    }
}
