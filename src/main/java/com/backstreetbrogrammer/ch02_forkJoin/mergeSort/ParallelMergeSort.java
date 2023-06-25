package com.backstreetbrogrammer.ch02_forkJoin.mergeSort;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort extends RecursiveAction {

    private final long[] nums;

    public ParallelMergeSort(final long[] nums) {
        this.nums = nums;
    }

    @Override
    protected void compute() {
        if (nums.length <= 5000) {
            // sequential merge sort
            mergeSort(nums);
            return;
        }

        final int middleIndex = nums.length / 2;
        final long[] left = Arrays.copyOfRange(nums, 0, middleIndex);
        final long[] right = Arrays.copyOfRange(nums, middleIndex + 1, nums.length);

        final ParallelMergeSort task1 = new ParallelMergeSort(left);
        final ParallelMergeSort task2 = new ParallelMergeSort(right);

        invokeAll(task1, task2);

        merge(left, right, nums);
    }

    private void mergeSort(final long[] nums) {
        if (nums.length <= 1) {
            return;
        }

        final int middleIndex = nums.length / 2;

        final long[] left = Arrays.copyOfRange(nums, 0, middleIndex);
        final long[] right = Arrays.copyOfRange(nums, middleIndex + 1, nums.length);

        mergeSort(left);
        mergeSort(right);

        merge(left, right, nums);
    }

    private void merge(final long[] leftSubArray, final long[] rightSubArray, final long[] originalArray) {
        int i = 0, j = 0, k = 0;

        while (i < leftSubArray.length && j < rightSubArray.length) {
            if (leftSubArray[i] < rightSubArray[j]) {
                originalArray[k++] = leftSubArray[i++];
            } else {
                originalArray[k++] = rightSubArray[j++];
            }
        }

        while (i < leftSubArray.length) {
            originalArray[k++] = leftSubArray[i++];
        }

        while (j < rightSubArray.length) {
            originalArray[k++] = rightSubArray[j++];
        }
    }
}
