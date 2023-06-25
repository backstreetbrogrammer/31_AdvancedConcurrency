package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolSimpleDemo {

    /*
        fork()   - asynchronous execution of tasks in the pool
                   we call it when using RecursiveTask<T> or RecursiveAction
        join()   - returns the result when the computation is finished
        invoke() - synchronous execution of the given tasks + wait + return the result upon completion
     */

    public static void main(final String[] args) {
        final ForkJoinPool pool = new ForkJoinPool();
        final SimpleRecursiveActionForkJoin action = new SimpleRecursiveActionForkJoin(400);
        pool.invoke(action);
    }

}
