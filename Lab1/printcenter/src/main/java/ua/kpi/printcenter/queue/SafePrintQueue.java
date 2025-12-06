package ua.kpi.printcenter.queue;

import ua.kpi.printcenter.document.Document;

public class SafePrintQueue implements PrintQueue {
    private final Document[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;
    private final int capacity;
    private boolean closed = false;
    private int maxObservedSize = 0;

    public SafePrintQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        this.buffer = new Document[capacity];
    }

    @Override
    public synchronized void submit(Document doc) throws InterruptedException {
        while (!closed && count >= capacity) {
            wait();
        }
        if (closed) {
            throw new IllegalStateException("Queue is closed for new documents");
        }
        buffer[tail] = doc;
        tail = (tail + 1) % capacity;
        count++;
        if (count > maxObservedSize) maxObservedSize = count;
        notifyAll();
    }

    @Override
    public synchronized Document take() throws InterruptedException {
        while (count == 0 && !closed) {
            wait();
        }
        if (count == 0) {
            return null;
        }
        Document doc = buffer[head];
        buffer[head] = null;
        head = (head + 1) % capacity;
        count--;
        notifyAll();
        return doc;
    }

    @Override
    public synchronized int size() { return count; }

    @Override
    public synchronized void shutdown() {
        closed = true;
        notifyAll();
    }

    @Override
    public synchronized int getMaxObservedSize() { return maxObservedSize; }
}
