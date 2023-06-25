package com.backstreetbrogrammer.ch02_forkJoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ForkJoinPool;

public class SimpleRecursiveTaskTest {

    @ParameterizedTest
    @ValueSource(doubles = {
            400D,
            70D
    })
    @DisplayName("Test RecursiveTask using fork-join")
    void testRecursiveTaskUsingForkJoin(final double num) {
        final ForkJoinPool pool = new ForkJoinPool();
        final SimpleRecursiveTask task = new SimpleRecursiveTask(num);
        final double squareOfNum = pool.invoke(task);
        System.out.printf("%.2f%n------------------------%n%n", squareOfNum);
    }

}
