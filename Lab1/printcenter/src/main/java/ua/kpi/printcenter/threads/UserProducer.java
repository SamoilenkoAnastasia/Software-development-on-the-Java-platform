package ua.kpi.printcenter.threads;

import ua.kpi.printcenter.document.Document;
import ua.kpi.printcenter.document.DocType;
import ua.kpi.printcenter.queue.PrintQueue;

import java.util.Random;

public class UserProducer implements Runnable {
    private final String userName;
    private final PrintQueue queue;
    private final int jobsToSend;
    private final int minPages;
    private final int maxPages;
    private final long delayMinMs;
    private final long delayMaxMs;
    private final Random rnd = new Random();

    public UserProducer(String userName, PrintQueue queue,
                        int jobsToSend, int minPages, int maxPages,
                        long delayMinMs, long delayMaxMs) {
        this.userName = userName;
        this.queue = queue;
        this.jobsToSend = jobsToSend;
        this.minPages = Math.max(1, minPages);
        this.maxPages = Math.max(this.minPages, maxPages);
        this.delayMinMs = Math.max(0, delayMinMs);
        this.delayMaxMs = Math.max(this.delayMinMs, delayMaxMs);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < jobsToSend && !Thread.currentThread().isInterrupted(); i++) {
                int pages = minPages + rnd.nextInt(maxPages - minPages + 1);
                DocType type = DocType.values()[rnd.nextInt(DocType.values().length)];
                Document doc = new Document(userName, type, pages);
                queue.submit(doc);
                System.out.printf("[User:%s] submitted %s%n", userName, doc);
                jitterSleep();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.printf("[User:%s] interrupted%n", userName);
        } catch (RuntimeException ex) {
            System.err.printf("[User:%s] error: %s%n", userName, ex.getMessage());
        }
    }

    private void jitterSleep() throws InterruptedException {
        long span = Math.max(0L, delayMaxMs - delayMinMs);
        long pause = delayMinMs + (span > 0 ? rnd.nextInt((int)Math.min(span, Integer.MAX_VALUE)) : 0);
        Thread.sleep(pause);
    }
}
