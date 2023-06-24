package com.backstreetbrogrammer.ch02_forkJoin;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintIntegersInParallelTest {

    @Test
    void testPrintingIntegersInParallel() {
        final var myList = Stream.iterate(1, n -> n + 1)
                                 .limit(10L)
                                 .collect(Collectors.toList());
        final PrintIntegersInParallel printAction = new PrintIntegersInParallel(myList);
        printAction.invoke();
    }
}
