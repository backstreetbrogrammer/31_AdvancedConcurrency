# Advanced Concurrency

> This is a tutorials course covering advanced concurrency in Java.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. [Executor Pattern, Callable and Future](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-01-executor-pattern-callable-and-future)
2. [Fork/Join Framework](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-02-forkjoin-framework)
3. [Advanced Locking and Semaphores](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-03-advanced-locking-and-semaphores)
4. [Using Barriers and Latches](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-04-using-barriers-and-latches)
5. [CAS operation and Atomic classes](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-05-cas-operation-and-atomic-classes)
6. [Concurrent Collections](https://github.com/backstreetbrogrammer/31_AdvancedConcurrency#chapter-06-concurrent-collections)

---

### Chapter 01. Executor Pattern, Callable and Future

#### Executor Pattern

The naive way to create a thread and start is by using `Runnable` interface.

Code snippet:

```
final Runnable task = () -> System.out.println("Hello students!");
final Thread thread = new Thread(task);
thread.start();
```

- the thread task is wrapped in an instance of Runnable `run()` method
- Runnable instance is passed to a new instance of Thread in constructor
- Thread is executed by calling `start()`

Downsides of this approach:

- a thread is created on demand by the user - means that any user can create any number of threads in the application
- **problem**: most of the operating systems have a limit to the maximum number of threads which can be created
- once the task is done, i.e. finish the `run()` method => the thread dies
- **problem**: a thread is an expensive resource, and it will impact system performance if too many threads are created
  and finished-terminated at a quick rate

The **Executor Pattern** aims to fix the above-mentioned issues:

- by creating pools of ready-to-use threads
- passing task to this pool of threads that will execute it

```java
public interface Executor {
    void execute(Runnable task);
}
```

A pool of thread is an instance of the `Executor` interface.

```java
public interface ExecutorService extends Executor {
    // 11 more methods
}
```

`ExecutorService` is an extension of `Executor` and has got around 12 more methods. The implementations of both
interfaces are the same. The factory class `Executors` proposes around 20 more methods to **create executors**.

Code snippet to create a single-thread pool:

```
final ExecutorService executor = Executors.newSingleThreadExecutor();
final Runnable task = () -> System.out.println("Hello students!");

// Executor pattern
executor.execute(task);

// Runnable pattern
new Thread(task).start();
```

The thread in this pool will be kept alive as long as this pool is alive. It means that the single thread will execute
the submitted task => once the task finishes, the thread will return to the pool and wait for new task to be submitted
for execution.

As compared to Runnable pattern, Executor pattern does NOT create a new thread. However, the behavior is same: both
calls return immediately and task is executed in **another** thread.

We can also create executors with multiple threads in the pool:

```
// fixed thread pool size of 4 threads
final ExecutorService multipleThreadsExecutor = Executors.newFixedThreadPoolExecutor(4);

// cached thread pool where size is dependent on number of system cpu cores available 
// creates new threads as needed, but will reuse previously constructed threads when they are available
final ExecutorService cachedExecutor = Executors.newCachedThreadPool();
```

More about **cached** thread pool:

- create threads **on demand** but will reuse previously constructed threads when they are available
- keeps **unused** threads for **60 seconds** by default, then terminates them

**Waiting queue**

Suppose we have a code snippet:

```
final Executor executor = Executors.newSingleThreadExecutor();

final Runnable task1 = () -> someLongProcess();
final Runnable task2 = () -> anotherLongProcess();

executor.execute(task1);
executor.execute(task2);
```

When we run this code, **task2** has to wait for **task1** to complete. The executor has a **waiting queue** to handle
this:

- A task is added to the waiting queue when **no** thread is available
- The tasks are executed in the **order** of their submission

**Can we know if a task is done or not?**

**Answer is no** - we don't have any API method in **ExecutorService** to check that. However, we can have a print
statement at the end of our Runnable task (`run()` method) to indicate that task has completed.

**Can we cancel the execution of a task?**

**Answer is yes** - BUT only if the task has NOT started yet and still in the waiting queue.

To summarize the advantages of using **Executor Pattern**:

- building an executor is **more performance efficient** than creating threads on demand
- can pass instances of Runnable to an executor - even if there are no threads available in the pool, executor has a
  waiting queue to **handle more tasks** than number of threads in the pool
- a task can be cancelled by removing it from the waiting queue

#### Callable and Future

There are some caveats in `Runnable` interface:

```java

@FunctionalInterface
public interface Runnable {
    void run();
}
```

There is no way we can know if a task is done or not.

- No object can be returned
- No exception can be raised

We need to know if there was any exception raised by the task and also we may need flexibility to get a value returned
from the task.

In Java 1.5 release, new `Callable` interface was introduced:

```java

@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```

As seen, `Callable` can return `V` object and also throws `Exception` (if any) to the calling thread.

We can use it via `submit()` method in `ExecutorService` which returns a `Future` object:

```
<T> Future<T> submit(Callable<T> task);
```

Sample code snippet:

```
// In the main thread
final ExecutorService executor = Executors.newFixedThreadPoolExecutor(4);
final Callable<String> task = () -> someTaskWhichReturnsString();

final Future<String> future = executor.submit(task); // asynchronous
// optionally, main thread can do some other task here
// ...
final String result = future.get(); // get() call is blocking until the returned String is available 
```

More about `Future.get()` method:

- blocking until the returned value `V` is available
- can raise 2 exceptions:
    - if the thread from the thread pool in `ExecutorService` is interrupted => throws `InterruptedException`
    - if the submitted task itself throws an exception => it is wrapped in an `ExecutionException` and re-thrown

We can use overloaded `Future.get()` methods which allows to pass a **timeout** as argument to avoid indefinitely
blocking calls. However, if the value `V` is still not available after the given timeout, it will throw
`TimeoutException`.

Other methods in `Future`:

- **isDone()**: tells us if the executor has finished processing the task. If the task is complete, it will return true;
  otherwise, it returns false.
- **cancel(boolean)**: can be used to tell the executor to stop the operation and interrupt its underlying thread.
- **isCancelled()**: tells us if the task is cancelled or not, returning a boolean.

Now as we have learnt basics of `Callable` and `Future`, let's explore more methods available in `Executor`.

ExecutorService can execute both `Runnable` and `Callable` tasks.

Example code snippet:

```
final Runnable runnableTask = () -> {
    try {
        TimeUnit.MILLISECONDS.sleep(300L);
        System.out.println("Hello students from Runnable!");
    } catch (final InterruptedException e) {
        e.printStackTrace();
    }
};

final Callable<String> callableTask = () -> {
    TimeUnit.MILLISECONDS.sleep(300L);
    return "Hello students from Callable!";
};

final List<Callable<String>> callableTasks = List.of(callableTask, callableTask, callableTask);
final ExecutorService executorService = Executors.newFixedThreadPoolExecutor(2);
```

We can call following methods:

- **execute()**: method is `void` and doesn't give any possibility to get the result of a task's execution or to check
  the task's status (if it is running):
    - `executorService.execute(runnableTask);`
- **submit()**: submits a `Callable` or a `Runnable` task to an `ExecutorService` and returns a result of type `Future`:
    - `Future<String> future = executorService.submit(callableTask);`
    - `Future<?> future = executorService.submit(runnableTask);` // Future's `get()` method will return `null` upon
      successful completion
- **invokeAny()**: assigns a collection of tasks to an `ExecutorService`, causing each to run, and returns the result of
  a successful execution of **one** task (if there was a successful execution)
    - `String result = executorService.invokeAny(callableTasks);`
- **invokeAll()**: assigns a collection of tasks to an `ExecutorService`, causing each to run, and returns the result of
  **all** task executions in the form of a list of objects of type `Future`
    - `List<Future<String>> futures = executorService.invokeAll(callableTasks);`

**Shutting Down an ExecutorService**

The `ExecutorService` will not be automatically destroyed when there is no task to process. It will stay alive and wait
for new work to do. So, the application or main thread will not terminate as the threads in the executor's thread pool
are **non-daemon** which will keep the application alive.

To properly shut down an `ExecutorService`, we have the `shutdown()` and `shutdownNow()` methods.

- **shutdown()**: method doesn't cause immediate destruction of the `ExecutorService`. It will make the
  `ExecutorService` stop accepting new tasks and shut down after all running threads finish their current work.
    - `executorService.shutdown();`
- **shutdownNow()**: method tries to destroy the `ExecutorService` immediately, but it doesn't guarantee that all the
  running threads will be stopped at the same time.
    - `List<Runnable> notExecutedTasks = executorService.shutDownNow();` // This method returns a list of tasks that are
      waiting to be processed. It is up to the developer to decide what to do with these tasks.

**Oracle** recommended way to shut down the `ExecutorService` using `awaitTermination()` method:

```
executorService.shutdown();
try {
    if (!executorService.awaitTermination(900L, TimeUnit.MILLISECONDS)) {
        executorService.shutdownNow();
    } 
} catch (final InterruptedException e) {
    executorService.shutdownNow();
}
```

`ExecutorService` will first stop taking new tasks and then wait up to a specified period of time (900 ms) for all tasks
to be completed. If that time expires, the execution is stopped immediately.

#### Interview Problem 1 (Barclays): Implement GCD algorithm using Executor Service and Futures

Greatest Common Divisor (GCD) of two or more integers is the largest integer that divides each of the integers such that
their remainder is zero.

Example:

- GCD of 20, 30 = 10  (10 is the largest number which divides 20 and 30 with remainder as 0)
- GCD of 42, 120, 285 = 3  (3 is the largest number which divides 42, 120 and 285 with remainder as 0)

Pseudo Code of the Euclidean Algorithm to find GCD of 2 numbers:

- Step 1:  Let `a, b` be the two numbers
- Step 2:  `a mod b = R`
- Step 3:  Let `a = b` and `b = R`
- Step 4:  Repeat Steps 2 and 3 until `a mod b` is greater than `0`
- Step 5:  `GCD = a`
- Step 6: Finish

Write the GCD algorithm in Java using Executor Service and Futures.

**Solution**:

Let's implement the GCD algorithm as given in the pseudocode:

```
    // Euclidean Algorithm
    private static int gcd(final int a, final int b) {
        if (b == 0)
            return a;

        return gcd(b, a % b);
    }
```

Complete code using Futures:

```java
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
```

Output:

```
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
GCD of (20,30) is 10
GCD of (15,35) is 5
```

**ScheduledExecutorService**

The `ScheduledExecutorService` runs tasks after some predefined delay and/or periodically. We can create a new
`ScheduledExecutorService` instance using the `Executors` factory method:

```
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
```

Methods available:

- **schedule()**: schedule a single task's execution after a fixed delay; tasks can be `Callable` or `Runnable`
    - `Future<String> resultFuture = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);` // delays for one
      second before executing **callableTask**
- **scheduleAtFixedRate()**: schedule a single task's execution after a fixed delay and then keep on running at a fixed
  rate. Following block of code will run a task after an initial delay of 100 ms and after that, it will run the same
  task every 450 ms:
    - `executorService.scheduleAtFixedRate(runnableTask, 100L, 450L, TimeUnit.MILLISECONDS);`
- **scheduleWithFixedDelay()**: if it is necessary to have a fixed length delay between iterations of the task.
  Following code will guarantee a 150 ms pause between the end of the current execution and the start of another one:
    - `executorService.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);`

**Few common pitfalls of ExecutorService**

- Keeping an unused `ExecutorService` alive: We need to be careful and shut down an ExecutorService, otherwise the
  application will keep on running even if it has completed.
- Wrong thread-pool capacity while using fixed length thread pool: It is very important to determine how many threads
  the application will need to run tasks efficiently. A too-large thread pool will cause unnecessary overhead just to
  create threads that will mostly be in the waiting mode. Too few can make an application seem unresponsive because of
  long waiting periods for tasks in the queue.
- Calling a Future‘s `get()` method after task cancellation: Attempting to get the result of an already canceled task
  triggers a `CancellationException`.
- Unexpectedly long blocking with Future‘s `get()` method: We should use timeouts to avoid unexpected waits.

---

### Chapter 02. Fork/Join Framework

The Fork/Join Framework was designed to recursively split a parallelizable task into smaller tasks and then combine the
results of each subtask to produce the overall result. It provides tools to help speed up parallel processing by
attempting to use all available processor cores. It accomplishes this through a divide and conquer approach.

Pseudo code:

```
if (task is small enough or no longer divisible) {
    compute task sequentially
} else {
    split task in 2 subtasks
    call this method recursively possibly further splitting each subtask
    wait for the completion of all subtasks
    combine the results of each subtask
}
```

In practice, this means that the framework first **"forks"** recursively breaking the task into smaller independent
subtasks until they are simple enough to run asynchronously.

After that, the **"join"** part begins. The results of all subtasks are recursively joined into a single result. In the
case of a task that returns `void`, the program simply waits until every subtask runs.

To provide effective parallel execution, the fork/join framework uses a pool of threads called the `ForkJoinPool`. This
pool manages worker threads of type `ForkJoinWorkerThread`.

**ExecutorService vs Fork/Join**

After the release of Java 7, many developers decided to replace the `ExecutorService` framework with the **Fork/Join
framework**.

However, despite the simplicity and frequent performance gains associated with fork/join, it reduces developer control
over concurrent execution.

`ExecutorService` gives the developer the ability to control the number of generated threads and the granularity of
tasks that should be run by separate threads. The best use case for `ExecutorService` is the processing of
**independent** tasks, such as transactions or requests according to the scheme **"one thread for one task"**.

In contrast, **fork/join** was designed to speed up work that can be broken into smaller pieces recursively.

---

### Chapter 03. Advanced Locking and Semaphores

---

### Chapter 04. Using Barriers and Latches

---

### Chapter 05. CAS operation and Atomic classes

---

### Chapter 06. Concurrent Collections

