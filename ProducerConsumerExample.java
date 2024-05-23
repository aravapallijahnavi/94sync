import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.LinkedList;
import java.util.Queue;

class BlockingQueueProducer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int limit;

    public BlockingQueueProducer(BlockingQueue<Integer> queue, int limit) {
        this.queue = queue;
        this.limit = limit;
    }

    @Override
    public void run() {
        for (int i = 0; i < limit; i++) {
            try {
                queue.put(i);
                System.out.println("BlockingQueue Produced: " + i);
                Thread.sleep(50); // simulate time taken to produce
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class BlockingQueueConsumer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int limit;

    public BlockingQueueConsumer(BlockingQueue<Integer> queue, int limit) {
        this.queue = queue;
        this.limit = limit;
    }

    @Override
    public void run() {
        for (int i = 0; i < limit; i++) {
            try {
                Integer item = queue.take();
                System.out.println("BlockingQueue Consumed: " + item);
                Thread.sleep(150); // simulate time taken to consume
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class WaitNotifyProducer implements Runnable {
    private final Queue<Integer> queue;
    private final int MAX_CAPACITY;
    private final int limit;

    public WaitNotifyProducer(Queue<Integer> queue, int maxCapacity, int limit) {
        this.queue = queue;
        this.MAX_CAPACITY = maxCapacity;
        this.limit = limit;
    }

    @Override
    public void run() {
        for (int i = 0; i < limit; i++) {
            synchronized (queue) {
                while (queue.size() == MAX_CAPACITY) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                queue.add(i);
                System.out.println("WaitNotify Produced: " + i);
                queue.notifyAll();
                try {
                    Thread.sleep(50); // simulate time taken to produce
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

class WaitNotifyConsumer implements Runnable {
    private final Queue<Integer> queue;
    private final int limit;

    public WaitNotifyConsumer(Queue<Integer> queue, int limit) {
        this.queue = queue;
        this.limit = limit;
    }

    @Override
    public void run() {
        for (int i = 0; i < limit; i++) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                int item = queue.poll();
                System.out.println("WaitNotify Consumed: " + item);
                queue.notifyAll();
                try {
                    Thread.sleep(150); // simulate time taken to consume
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

public class ProducerConsumerExample {
    public static void main(String[] args) {
        int limit = 5;

        // Using BlockingQueue
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(5);
        Thread blockingQueueProducerThread = new Thread(new BlockingQueueProducer(blockingQueue, limit));
        Thread blockingQueueConsumerThread = new Thread(new BlockingQueueConsumer(blockingQueue, limit));

        blockingQueueProducerThread.start();
        blockingQueueConsumerThread.start();

        // Using wait() and notify()
        Queue<Integer> waitNotifyQueue = new LinkedList<>();
        int maxCapacity = 5;
        Thread waitNotifyProducerThread = new Thread(new WaitNotifyProducer(waitNotifyQueue, maxCapacity, limit));
        Thread waitNotifyConsumerThread = new Thread(new WaitNotifyConsumer(waitNotifyQueue, limit));

        waitNotifyProducerThread.start();
        waitNotifyConsumerThread.start();
    }
}
