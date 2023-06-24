package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.concurrent.RecursiveAction;

public class SimpleRecursiveActionForkJoin extends RecursiveAction {
    private final int taskSize;

    public SimpleRecursiveActionForkJoin(final int taskSize) {
        this.taskSize = taskSize;
    }

    @Override
    protected void compute() {
        // if the task is large, split it and execute in parallel
        if (taskSize > 100) {
            System.out.printf("[%s] Split the tasks [taskSize=%d] and execute in parallel%n",
                              Thread.currentThread().getName(), taskSize);
            final SimpleRecursiveActionForkJoin action1 = new SimpleRecursiveActionForkJoin(taskSize / 2);
            final SimpleRecursiveActionForkJoin action2 = new SimpleRecursiveActionForkJoin(taskSize / 2);

            action1.fork();
            action2.fork();

            action1.join();
            action2.join();
        } else {
            System.out.printf("[%s] Task [taskSize=%d] is small to be executed in sequence%n", Thread.currentThread().getName(), taskSize);
            task();
        }
    }

    private void task() {
        // it can be any complex task or algorithm
        System.out.printf("[%s] The size of the task is %d%n", Thread.currentThread().getName(), taskSize);
    }
}
