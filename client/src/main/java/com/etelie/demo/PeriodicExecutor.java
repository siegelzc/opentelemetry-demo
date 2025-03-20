package com.etelie.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute a function periodically
 */
@SuppressWarnings({"BusyWait"})
public class PeriodicExecutor {

    private static final Logger log = LoggerFactory.getLogger(PeriodicExecutor.class);

    private final VoidSupplier function;
    private final long frequency;

    public PeriodicExecutor(long frequencyMillis, VoidSupplier function) {
        this.function = function;
        this.frequency = frequencyMillis;

        this.start();
    }

    private void start() {
        Thread thread = new Thread(() -> {
            while (true) {
                log.trace("{} triggered [{}]", PeriodicExecutor.class.getSimpleName(), function.getClass().getSimpleName());

                try {
                    this.function.run();
                    Thread.sleep(this.frequency);
                } catch (InterruptedException e) {
                    log.warn("{} thread interrupted", PeriodicExecutor.class.getSimpleName(), e);
                    Thread.currentThread().interrupt();
                }
            }
        });

        thread.setName(PeriodicExecutor.class.getSimpleName());
        /* The daemon=true setting tells the JVM it's okay to shut down while this thread is still running.
           We don't want that. */
        thread.setDaemon(false);
        thread.start();
    }


    @FunctionalInterface
    public static interface VoidSupplier {
        public void run();
    }

}