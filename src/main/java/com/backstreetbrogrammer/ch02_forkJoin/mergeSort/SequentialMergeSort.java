package com.backstreetbrogrammer.ch02_forkJoin.mergeSort;

import java.util.Arrays;

public class SequentialMergeSort {

    private final long[] nums;

    public SequentialMergeSort(final long[] nums) {
        this.nums = nums;
    }

    public void sequentialMergeSort() {
        mergeSort(nums);
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
