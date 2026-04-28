import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * LocalQueueDemo
 *
 * - Producer  : adds a message every 1 second
 * - Consumer  : reads from the queue every 3 seconds (intentionally slower so messages pile up)
 * - Inspector : peeks at what's sitting in the queue every 2 seconds without consuming
 *
 * Run with:  javac src/LocalQueueDemo.java -d out && java -cp out LocalQueueDemo
 */
public class LocalQueueDemo {

    // Shared queue — capacity 10, blocks producer when full
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {

        Thread producer  = new Thread(new Producer(),  "Producer");
        Thread consumer  = new Thread(new Consumer(),  "Consumer");
        Thread inspector = new Thread(new Inspector(), "Inspector");

        producer.start();
        consumer.start();
        inspector.start();

        // Let it run for 15 seconds then stop
        Thread.sleep(15_000);

        producer.interrupt();
        consumer.interrupt();
        inspector.interrupt();

        System.out.println("\n[Main] Stopped. Messages still in queue: " + queue);
    }

    // ─── Producer ────────────────────────────────────────────────────────────
    static class Producer implements Runnable {
        private int count = 1;

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = "MSG-" + count++;
                    queue.put(message);   // blocks if queue is full
                    System.out.println("[Producer]  Added   → " + message
                            + "  | queue size: " + queue.size());
                    Thread.sleep(1000);   // produce every 1s
                }
            } catch (InterruptedException e) {
                System.out.println("[Producer]  Stopped.");
            }
        }
    }

    // ─── Consumer ────────────────────────────────────────────────────────────
    static class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // poll with timeout so interrupt can still stop the thread
                    String message = queue.poll(4, TimeUnit.SECONDS);
                    if (message != null) {
                        System.out.println("[Consumer]  Consumed ← " + message
                                + "  | queue size: " + queue.size());
                    }
                    Thread.sleep(3000);   // consume every 3s (slower than producer)
                }
            } catch (InterruptedException e) {
                System.out.println("[Consumer]  Stopped.");
            }
        }
    }

    // ─── Inspector ───────────────────────────────────────────────────────────
    // peek() / toArray() never removes anything — just looks at what's in the queue
    static class Inspector implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(2000);  // inspect every 2s

                    System.out.println("[Inspector] Queue size: " + queue.size()
                            + " | head (peek): " + queue.peek()
                            + " | all: " + queue);
                }
            } catch (InterruptedException e) {
                System.out.println("[Inspector] Stopped.");
            }
        }
    }
}
