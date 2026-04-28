# Multithreading & Concurrency

## Creating Threads

**Option 1 — Extend Thread:**
```java
class MyThread extends Thread {
    public void run() { System.out.println("running"); }
}
new MyThread().start();
```

**Option 2 — Implement Runnable (preferred):**
```java
Runnable task = () -> System.out.println("running");
new Thread(task).start();
```

**Option 3 — Callable (returns result):**
```java
Callable<Integer> task = () -> 42;
Future<Integer> future = executor.submit(task);
future.get(); // 42 — blocks until done
```

---

## Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → BLOCKED/WAITING/TIMED_WAITING → TERMINATED
```

---

## Thread Methods

```java
thread.start()          // start thread (calls run() internally)
thread.run()            // DON'T call directly — runs on current thread
thread.join()           // wait for thread to finish
thread.sleep(1000)      // pause current thread (ms)
thread.interrupt()      // signal thread to stop
thread.isAlive()
Thread.currentThread()  // reference to running thread
```

---

## synchronized

Prevents concurrent access to a critical section.

```java
// synchronized method
public synchronized void increment() { count++; }

// synchronized block (finer control)
synchronized(this) { count++; }
```

---

## volatile

Ensures visibility of a variable across threads. Reads/writes go directly to main memory.

```java
private volatile boolean running = true;
```
Does not guarantee atomicity (use `AtomicInteger` for that).

---

## ExecutorService

Manages a pool of threads. Preferred over manually creating threads.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> System.out.println("task"));
executor.shutdown();       // no new tasks, finish current
executor.shutdownNow();    // attempt to stop all tasks
```

**Common executors:**
| Method | Behavior |
|---|---|
| `newFixedThreadPool(n)` | Fixed n threads |
| `newCachedThreadPool()` | Grows as needed, reuses idle threads |
| `newSingleThreadExecutor()` | Single thread, sequential |
| `newScheduledThreadPool(n)` | Scheduled/periodic tasks |

---

## Atomic Classes

Thread-safe operations without `synchronized`.

```java
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();   // thread-safe ++
count.getAndIncrement();   // thread-safe, returns old value
count.compareAndSet(5, 10); // CAS operation
```

---

## ConcurrentHashMap

Thread-safe HashMap. Allows concurrent reads and segment-level writes.

```java
Map<String, Integer> map = new ConcurrentHashMap<>();
```

Better than `Collections.synchronizedMap()` under high concurrency.

---

## Common Interview Questions

**Q: `start()` vs `run()`?**
`start()` creates a new thread and calls `run()` on it. Calling `run()` directly executes it on the **current** thread — no new thread created.

**Q: What is a race condition?**
When two threads access shared data simultaneously and the result depends on execution order.

**Q: What is a deadlock?**
Two threads each hold a lock the other needs — both wait forever. Prevention: always acquire locks in the same order.

**Q: `synchronized` vs `Lock`?**
`Lock` (from `java.util.concurrent.locks`) offers more control: tryLock, timed lock, interruptible lock. `synchronized` is simpler but less flexible.

**Q: What is the difference between `wait()` and `sleep()`?**
| `wait()` | `sleep()` |
|---|---|
| From `Object` class | From `Thread` class |
| Releases the lock | Holds the lock |
| Used for inter-thread communication | Used for pausing |
| Must be in synchronized block | Can be used anywhere |

**Q: What is `volatile` used for?**
Guarantees that changes to a variable are visible to all threads immediately. Does not guarantee atomicity.

**Q: What is a thread-safe class?**
A class whose methods behave correctly when accessed from multiple threads simultaneously (e.g., `ConcurrentHashMap`, `AtomicInteger`, `Vector`).
