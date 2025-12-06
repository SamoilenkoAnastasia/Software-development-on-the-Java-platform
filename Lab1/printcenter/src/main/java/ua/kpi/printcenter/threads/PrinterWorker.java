package ua.kpi.printcenter.threads;

import ua.kpi.printcenter.document.Document;
import ua.kpi.printcenter.document.DocType;
import ua.kpi.printcenter.exceptions.PrinterOverheatException;
import ua.kpi.printcenter.queue.PrintQueue;

public class PrinterWorker implements Runnable {
    private final String id;
    private final PrintQueue queue;
    private final long baseMillisPerPage;
    private final int overheatThresholdPages;
    private int pagesSinceCooldown = 0;

    private int printedDocuments = 0;
    private int printedPages = 0;
    private long totalPrintMillis = 0;
    private int overheatCount = 0;

    public PrinterWorker(String id, PrintQueue queue, long baseMillisPerPage, int overheatThresholdPages) {
        this.id = id;
        this.queue = queue;
        this.baseMillisPerPage = baseMillisPerPage;
        this.overheatThresholdPages = overheatThresholdPages;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Document doc = queue.take();
                if (doc == null) {
                    System.out.printf("(Printer:%s) shutdown%n", id);
                    break;
                }
                System.out.printf("(Printer:%s) start %s%n", id, doc);
                long start = System.currentTimeMillis();
                for (int p = 0; p < doc.getPages(); p++) {
                    long millis = computeMillisPerPage(doc.getType());
                    Thread.sleep(millis);
                    printedPages++;
                    pagesSinceCooldown++;
                    checkOverheat();
                }
                long took = System.currentTimeMillis() - start;
                totalPrintMillis += took;
                printedDocuments++;
                System.out.printf("(Printer:%s) finished %s in %dms%n", id, doc, took);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.printf("(Printer:%s) interrupted%n", id);
        } catch (PrinterOverheatException ex) {
            overheatCount++;
            System.err.printf("(Printer:%s) OVERHEAT: %s - cooling...%n", id, ex.getMessage());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            pagesSinceCooldown = 0;
            run();
        } finally {
            printStats();
        }
    }

    private long computeMillisPerPage(DocType type) {
        return switch (type) {
            case TEXT -> baseMillisPerPage;
            case PDF -> baseMillisPerPage * 2;
            case PHOTO -> baseMillisPerPage * 4;
        };
    }

    private void checkOverheat() throws PrinterOverheatException {
        if (overheatThresholdPages > 0 && pagesSinceCooldown >= overheatThresholdPages) {
            throw new PrinterOverheatException("printed " + pagesSinceCooldown + " pages without cooldown");
        }
    }

    private void printStats() {
        System.out.printf("(Printer:%s) stats: docs=%d pages=%d totalMillis=%d overheat=%d%n",
                id, printedDocuments, printedPages, totalPrintMillis, overheatCount);
    }
}
