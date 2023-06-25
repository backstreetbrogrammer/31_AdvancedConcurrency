package com.backstreetbrogrammer.ch02_forkJoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ForkJoinPool;

public class FibonacciTaskTest {

    private ForkJoinPool pool;

    @BeforeEach
    void setUp() {
        pool = new ForkJoinPool();
    }

    @ParameterizedTest
    @ValueSource(longs = {
            25L,
            10L,
            6L
    })
    @DisplayName("Test to calculate the n-th Fibonacci-numbers")
    void testCalculateNthFibonacciNumber(final long num) {
        final FibonacciTask task = new FibonacciTask(num);
        final long nthFibNum = pool.invoke(task);
        System.out.printf("[%d]th Fibonacci Number = [%d]%n------------------------%n%n", num, nthFibNum);
    }

    @ParameterizedTest
    @ValueSource(longs = {
            25L,
            10L,
            6L
    })
    @DisplayName("Test to calculate the n-th Fibonacci-numbers in optimised way")
    void testCalculateNthFibonacciNumberOptimised(final long num) {
        final OptimisedFibonacciTask task = new OptimisedFibonacciTask(num);
        final long nthFibNum = pool.invoke(task);
        System.out.printf("[%d]th Fibonacci Number = [%d]%n------------------------%n%n", num, nthFibNum);
    }

}
