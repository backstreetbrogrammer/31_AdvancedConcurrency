package com.backstreetbrogrammer.ch02_forkJoin.maxFind;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;

public class ParallelMaxFindingTest {

    private static final int dataSize = 500_000_000;
    private static final long[] data = new long[dataSize];
    private final int noOfIterations = 10;

    @BeforeAll
    static void beforeAll() {
        for (int i = 0; i < dataSize; i++) {
            data[i] = ThreadLocalRandom.current().nextLong(dataSize);
        }
    }

    @Test
    @DisplayName("Test SequentialMaxFinding")
    void testSequentialMaxFinding() {
        final SequentialMaxFinding maxFinding = new SequentialMaxFinding(data);
        final var start = Instant.now();
        long max = 0L;
        for (int i = 0; i < noOfIterations; i++) {
            max = maxFinding.max();
        }
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis()) / noOfIterations;
        System.out.printf("[SequentialMaxFinding] time taken: %d ms for size: %d; MAX=[%d]%n%n",
                          timeElapsed, dataSize, max);
        System.out.println("---------------------------------------");
    }

    @Test
    @DisplayName("Test ParallelMaxFinding")
    void testParallelMaxFinding() {
        final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        final ParallelMaxFinding maxFinding = new ParallelMaxFinding(data, 0, data.length);
        final var start = Instant.now();
        long max = 0L;
        for (int i = 0; i < noOfIterations; i++) {
            max = pool.invoke(maxFinding);
        }
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis()) / noOfIterations;
        System.out.printf("[ParallelMaxFinding] time taken: %d ms for size: %d; MAX=[%d]%n%n",
                          timeElapsed, dataSize, max);
        System.out.println("---------------------------------------");
    }

}
