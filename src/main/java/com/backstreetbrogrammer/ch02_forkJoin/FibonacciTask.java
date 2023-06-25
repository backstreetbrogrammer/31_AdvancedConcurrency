package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.concurrent.RecursiveTask;

public class FibonacciTask extends RecursiveTask<Long> {

    private final long num;

    public FibonacciTask(final long num) {
        this.num = num;
    }

    @Override
    protected Long compute() {
        // F(0) = F(1) = 0
        if (num <= 1L)
            return num;

        // F(N) = F(N-1) + F(N-2)
        final FibonacciTask fib1 = new FibonacciTask(num - 1);
        final FibonacciTask fib2 = new FibonacciTask(num - 2);

        fib1.fork();
        fib2.fork();

        return fib1.join() + fib2.join();
    }
}
