package com.cafe.cafeapp.counter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class RaceConditionTest {

    private static final int THREADS = 60;
    private static final int OPERATIONS = 2000;

    @Test
    void testUnsafeCounter() throws Exception {

        UnsafeCounter counter = new UnsafeCounter();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    counter.increment();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        long expected = (long) THREADS * OPERATIONS;
        long actual = counter.getValue();

        System.out.println("\nUnsafeCounter");
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + actual);
    }

    @Test
    void testSynchronizedCounter() throws Exception {

        SynchronizedCounter counter = new SynchronizedCounter();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    counter.increment();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        long expected = (long) THREADS * OPERATIONS;
        long actual = counter.getValue();

        System.out.println("\nSynchronizedCounter");
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + actual);
    }

    @Test
    void testAtomicCounter() throws Exception {

        AtomicCounter counter = new AtomicCounter();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    counter.increment();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        long expected = (long) THREADS * OPERATIONS;
        long actual = counter.getValue();

        System.out.println("\nAtomicCounter");
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + actual);
    }
}