package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.concurrent.RecursiveTask;

public class OptimisedFibonacciTask extends RecursiveTask<Long> {

    private final long num;

    public OptimisedFibonacciTask(final long num) {
        this.num = num;
    }

    @Override
    protected Long compute() {
        // F(0) = F(1) = 0
        if (num <= 1L)
            return num;

        // F(N) = F(N-1) + F(N-2)
        final OptimisedFibonacciTask fib1 = new OptimisedFibonacciTask(num - 1);
        final OptimisedFibonacciTask fib2 = new OptimisedFibonacciTask(num - 2);

        fib2.fork();

        return fib1.compute() + fib2.join();
    }
}
