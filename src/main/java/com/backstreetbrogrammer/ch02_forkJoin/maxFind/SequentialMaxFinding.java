package com.backstreetbrogrammer.ch02_forkJoin.maxFind;

public class SequentialMaxFinding {
    private final long[] nums;

    public SequentialMaxFinding(final long[] nums) {
        this.nums = nums;
    }

    // Time complexity: O(n)
    // assumption: nums[] is not sorted
    public long max() {
        long max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > max) {
                max = nums[i];
            }
        }
        return max;
    }
}
