package ua.kpi.printcenter.queue;

import ua.kpi.printcenter.queue.SafePrintQueue;
import static org.junit.jupiter.api.Assertions.*;

import ua.kpi.printcenter.document.Document;
import ua.kpi.printcenter.document.DocType;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class QueueTests {

    @Test
    void enqueueDequeueRoundTrip() throws Exception {
        SafePrintQueue q = new SafePrintQueue(2);
        Document d = new Document("Alice", DocType.TEXT, 2);

        Thread producer = new Thread(() -> {
            try {
                q.submit(d);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });

        CountDownLatch got = new CountDownLatch(1);
        final Document[] received = new Document[1];
        Thread consumer = new Thread(() -> {
            try {
                received[0] = q.take();
                got.countDown();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        producer.start();
        producer.join();
        got.await();
        q.shutdown();
        consumer.join();

        assertEquals(d, received[0]);
        assertTrue(q.getMaxObservedSize() <= 2);
    }

    @Test
    void enqueueTimeout() {
        SafePrintQueue q = new SafePrintQueue(1);
        Document d = new Document("Bob", DocType.PHOTO, 1);

        assertTimeout(Duration.ofMillis(200), () -> {
            q.submit(d);
        });
    }
}
