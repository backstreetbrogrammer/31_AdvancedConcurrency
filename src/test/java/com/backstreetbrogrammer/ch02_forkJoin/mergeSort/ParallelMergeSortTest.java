package com.backstreetbrogrammer.ch02_forkJoin.mergeSort;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;

public class ParallelMergeSortTest {
    private static final int dataSize = 100_000_000;
    private static final long[] data = new long[dataSize];

    @BeforeAll
    static void beforeAll() {
        for (int i = 0; i < dataSize; i++) {
            data[i] = ThreadLocalRandom.current().nextLong(dataSize);
        }
    }

    @Test
    @DisplayName("Test SequentialMergeSort")
    void testSequentialMergeSort() {
        final SequentialMergeSort mergeSort = new SequentialMergeSort(data);
        final var start = Instant.now();
        mergeSort.sequentialMergeSort();
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis());
        System.out.printf("[SequentialMergeSort] time taken: %d ms to sort array of size: %d%n%n",
                          timeElapsed, dataSize);
        System.out.println("---------------------------------------");
    }

    @Test
    @DisplayName("Test ParallelMergeSort")
    void testParallelMergeSort() {
        final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        final ParallelMergeSort mergeSort = new ParallelMergeSort(data);
        final var start = Instant.now();
        pool.invoke(mergeSort);
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis());
        System.out.printf("[ParallelMergeSort] time taken: %d ms to sort array of size: %d%n%n",
                          timeElapsed, dataSize);
        System.out.println("---------------------------------------");
    }
}
