package ua.kpi.printcenter.app;

import ua.kpi.printcenter.queue.PrintQueue;
import ua.kpi.printcenter.queue.SafePrintQueue;
import ua.kpi.printcenter.threads.PrinterWorker;
import ua.kpi.printcenter.threads.UserProducer;

import java.util.ArrayList;
import java.util.List;

public class AppSafe {
    public static void main(String[] args) throws InterruptedException {
        final int queueCapacity = 10;
        final int userCount = 3;
        final int printerCount = 2;
        final int jobsPerUser = 8;
        final int pagesMin = 1;
        final int pagesMax = 5;
        final long delayUserMinMs = 10;
        final long delayUserMaxMs = 50;
        final long baseMillisPerPage = 20;
        final int overheatThresholdPages = 40;

        PrintQueue queue = new SafePrintQueue(queueCapacity);
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= printerCount; i++) {
            Thread t = new Thread(new PrinterWorker("PR-" + i, queue, baseMillisPerPage, overheatThresholdPages),
                    "printer-" + i);
            t.start();
            threads.add(t);
        }

        List<Thread> producers = new ArrayList<>();
        for (int i = 1; i <= userCount; i++) {
            Thread t = new Thread(new UserProducer("User-" + i, queue, jobsPerUser, pagesMin, pagesMax, delayUserMinMs, delayUserMaxMs),
                    "user-" + i);
            t.start();
            producers.add(t);
            threads.add(t);
        }

        for (Thread p : producers) p.join();

        queue.shutdown();

        for (Thread t : threads) {
            if (t.getName().startsWith("printer-")) t.join();
        }

        System.out.printf("Safe run finished. Max observed queue size=%d (capacity=%d)%n", queue.getMaxObservedSize(), queueCapacity);
    }
}
