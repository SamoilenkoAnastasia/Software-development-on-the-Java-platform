package ua.kpi.printcenter.queue;

import ua.kpi.printcenter.document.Document;

public interface PrintQueue {
    void submit(Document doc) throws InterruptedException;
    Document take() throws InterruptedException;
    int size();
    void shutdown();
    int getMaxObservedSize();
}
