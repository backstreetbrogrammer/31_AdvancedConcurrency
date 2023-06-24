package com.backstreetbrogrammer.ch02_forkJoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class PrintIntegersInParallel extends RecursiveAction {

    private final List<Integer> nums;

    public PrintIntegersInParallel(final List<Integer> nums) {
        this.nums = nums;
    }

    @Override
    protected void compute() {
        // the problem is small enough (containing 2 items) => sequential print operation
        if (nums.size() < 2) {
            for (final Integer num : nums)
                System.out.printf("[%s]num=%d%n", Thread.currentThread().getName(), num);
        } else {
            /* otherwise, we split the problem into 2 sub-problems:
                   [a,b,c] --> [a] and [b,c]
                   [a,b,c,d] --> [a,b] and [c,d]
             */
            final List<Integer> left = nums.subList(0, nums.size() / 2);
            final List<Integer> right = nums.subList(nums.size() / 2, nums.size());

            final PrintIntegersInParallel action1 = new PrintIntegersInParallel(left);
            final PrintIntegersInParallel action2 = new PrintIntegersInParallel(right);

            invokeAll(action1, action2); // or can use fork(), join() too
        }
    }
}
