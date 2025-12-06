package ua.kpi.printcenter.app;

import ua.kpi.printcenter.queue.UnsafePrintQueue;
import ua.kpi.printcenter.document.Document;
import ua.kpi.printcenter.document.DocType;

import java.util.ArrayList;
import java.util.List;

public class AppUnsafe {
    public static void main(String[] args) throws InterruptedException {
        final int queueCapacity = 4;
        final int userCount = 6;
        final int printerCount = 3;
        final int jobsPerUser = 40;

        UnsafePrintQueue queue = new UnsafePrintQueue(queueCapacity);
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= printerCount; i++) {
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        var doc = queue.take();
                        if (doc != null) {
                            Thread.sleep(2);
                        } else {
                            Thread.yield();
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }, "unsafe-printer-" + i);
            t.start();
            threads.add(t);
        }

        for (int i = 1; i <= userCount; i++) {
            Thread t = new Thread(() -> {
                try {
                    for (int k = 0; k < jobsPerUser && !Thread.currentThread().isInterrupted(); k++) {
                        Document d = new Document(Thread.currentThread().getName(), DocType.TEXT, 1);
                        queue.submit(d);
                        Thread.sleep(1);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }, "unsafe-user-" + i);
            t.start();
            threads.add(t);
        }

        Thread.sleep(2000);
        for (Thread t : threads) t.interrupt();
        for (Thread t : threads) t.join();

        System.out.println("Unsafe run finished (race conditions likely occurred)");
    }
}
