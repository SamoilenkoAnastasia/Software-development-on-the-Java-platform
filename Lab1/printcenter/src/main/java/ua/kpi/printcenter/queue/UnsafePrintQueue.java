package ua.kpi.printcenter.queue;

import ua.kpi.printcenter.document.Document;

import java.util.LinkedList;
import java.util.List;

public class UnsafePrintQueue implements PrintQueue {
    private final List<Document> list = new LinkedList<>();
    private final int capacity;
    private boolean closed = false;
    private int maxObservedSize = 0;

    public UnsafePrintQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    @Override
    public void submit(Document doc) {
        if (closed) throw new IllegalStateException("Queue closed");
        if (list.size() >= capacity) {
            list.remove(0);
        }
        list.add(doc);
        if (list.size() > maxObservedSize) maxObservedSize = list.size();
    }

    @Override
    public Document take() {
        if (list.isEmpty()) return null;
        return list.remove(0);
    }

    @Override
    public int size() { return list.size(); }

    @Override
    public void shutdown() { closed = true; }

    @Override
    public int getMaxObservedSize() { return maxObservedSize; }
}
