package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.concurrent.RecursiveTask;

public class SimpleRecursiveTask extends RecursiveTask<Double> {
    private final double num;

    public SimpleRecursiveTask(final double num) {
        this.num = num;
    }

    @Override
    protected Double compute() {
        // if the number is large, split it and execute in parallel
        if (num > 100D) {
            System.out.printf("[%s] Split the tasks [num=%.2f] and execute in parallel%n",
                              Thread.currentThread().getName(), num);
            final SimpleRecursiveTask task1 = new SimpleRecursiveTask(num / 2);
            final SimpleRecursiveTask task2 = new SimpleRecursiveTask(num / 2);

            task1.fork();
            task2.fork();

            // wait for the tasks to complete
            double subResult = 0D;
            subResult += task1.join();
            subResult += task2.join();

            return subResult;
        } else {
            System.out.printf("[%s] Task [num=%.2f] is small to be executed in sequence%n",
                              Thread.currentThread().getName(), num);
            return task();
        }
    }

    private Double task() {
        // it can be any complex task or algorithm
        final double result = num * num;
        System.out.printf("[%s] The square of the num %.2f is %.2f%n", Thread.currentThread().getName(), num, result);
        return result;
    }
}
