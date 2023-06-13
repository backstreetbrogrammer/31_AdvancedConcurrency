package com.backstreetbrogrammer.ch01_executorPattern;

import java.util.concurrent.*;

public class GCDCalculator {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public Future<Integer> calculate(final int a, final int b) {
        return executor.submit(() -> {
            TimeUnit.MILLISECONDS.sleep(1000L);
            return gcd(a, b);
        });
    }

    // Euclidean Algorithm
    private static int gcd(final int a, final int b) {
        if (b == 0)
            return a;

        return gcd(b, a % b);
    }

    private void shutdown() {
        executor.shutdown();
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final GCDCalculator gcdCalculator = new GCDCalculator();

        final Future<Integer> future1 = gcdCalculator.calculate(20, 30);
        final Future<Integer> future2 = gcdCalculator.calculate(15, 35);

        while (!(future1.isDone() && future2.isDone())) {
            System.out.printf("future1 is %s and future2 is %s%n",
                              future1.isDone() ? "done" : "not done",
                              future2.isDone() ? "done" : "not done");
            TimeUnit.MILLISECONDS.sleep(300L);
        }

        final Integer result1 = future1.get();
        final Integer result2 = future2.get();

        System.out.printf("GCD of (20,30) is %d%n", result1);
        System.out.printf("GCD of (15,35) is %d%n", result2);

        gcdCalculator.shutdown();
    }
}
