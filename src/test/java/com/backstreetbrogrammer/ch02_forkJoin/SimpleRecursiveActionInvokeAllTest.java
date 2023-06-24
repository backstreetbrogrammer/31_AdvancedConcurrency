package com.backstreetbrogrammer.ch02_forkJoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SimpleRecursiveActionInvokeAllTest {

    @ParameterizedTest
    @ValueSource(ints = {
            400,
            70
    })
    @DisplayName("Test RecursiveAction with different task sizes using invokeAll()")
    void testRecursiveActionWithDifferentTaskSizesUsingInvokeAll(final int taskSize) {
        final SimpleRecursiveActionInvokeAll action = new SimpleRecursiveActionInvokeAll(taskSize);
        action.invoke();
        System.out.println("------------------------\n");
    }
}
