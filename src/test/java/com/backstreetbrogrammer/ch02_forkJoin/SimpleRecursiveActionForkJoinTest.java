package com.backstreetbrogrammer.ch02_forkJoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SimpleRecursiveActionForkJoinTest {

    @ParameterizedTest
    @ValueSource(ints = {
            400,
            70
    })
    @DisplayName("Test RecursiveAction with different task sizes using fork-join")
    void testRecursiveActionWithDifferentTaskSizesUsingForkJoin(final int taskSize) {
        final SimpleRecursiveActionForkJoin action = new SimpleRecursiveActionForkJoin(taskSize);
        action.invoke();
        System.out.println("------------------------\n");
    }
}
