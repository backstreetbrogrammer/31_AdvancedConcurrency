package com.backstreetbrogrammer.ch02_forkJoin.maxFind;

import java.util.concurrent.RecursiveTask;

public class ParallelMaxFinding extends RecursiveTask<Long> {
    private final long[] nums;
    private final int lowIndex;
    private final int highIndex;

    public ParallelMaxFinding(final long[] nums, final int lowIndex, final int highIndex) {
        this.nums = nums;
        this.lowIndex = lowIndex;
        this.highIndex = highIndex;
    }

    @Override
    protected Long compute() {
        // if the array is small - we can use sequential max finding algorithm
        if ((highIndex - lowIndex) < 5000) {
            return sequentialMaxFinding();
        } else {
            // use parallelization
            final int middleIndex = lowIndex + (highIndex - lowIndex) / 2;
            final ParallelMaxFinding task1 = new ParallelMaxFinding(nums, lowIndex, middleIndex);
            final ParallelMaxFinding task2 = new ParallelMaxFinding(nums, middleIndex + 1, highIndex);

            invokeAll(task1, task2);

            return Math.max(task1.join(), task2.join());
        }
    }

    // Time complexity: O(n)
    // assumption: nums[] is not sorted
    public Long sequentialMaxFinding() {
        long max = nums[lowIndex];
        for (int i = lowIndex + 1; i < highIndex; i++) {
            if (nums[i] > max) {
                max = nums[i];
            }
        }
        return max;
    }
}
