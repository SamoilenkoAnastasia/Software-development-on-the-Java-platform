package ua.kpi.printcenter.document;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class Document {
    private static final AtomicLong SEQ = new AtomicLong(1);

    private final long id;
    private final String owner;
    private final DocType type;
    private final int pages;
    private final long createdAt;

    public Document(String owner, DocType type, int pages) {
        this.id = SEQ.getAndIncrement();
        this.owner = Objects.requireNonNull(owner, "owner");
        this.type = Objects.requireNonNull(type, "type");
        if (pages <= 0) throw new IllegalArgumentException("pages must be > 0");
        this.pages = pages;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public String getOwner() { return owner; }
    public DocType getType() { return type; }
    public int getPages() { return pages; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Document{id=" + id + ", owner='" + owner + "', type=" + type + ", pages=" + pages + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document d = (Document) o;
        return id == d.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
