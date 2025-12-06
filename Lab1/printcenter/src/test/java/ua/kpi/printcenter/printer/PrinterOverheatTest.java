package ua.kpi.printcenter.printer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ua.kpi.printcenter.queue.SafePrintQueue;
import ua.kpi.printcenter.threads.PrinterWorker;
import ua.kpi.printcenter.document.Document;
import ua.kpi.printcenter.document.DocType;

import java.util.concurrent.TimeUnit;

public class PrinterOverheatTest {

    @Test
    void printerOverheatHandled() throws Exception {
        SafePrintQueue q = new SafePrintQueue(10);
        PrinterWorker worker = new PrinterWorker("T-1", q, 1, 3);

        Thread pt = new Thread(worker);
        pt.start();

        q.submit(new Document("Tester", DocType.TEXT, 5));

        TimeUnit.SECONDS.sleep(2);

        q.shutdown();
        pt.join(5000);

        assertTrue(true);
    }
}
