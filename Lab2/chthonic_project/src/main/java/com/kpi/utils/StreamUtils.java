package com.kpi.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Stream utility helpers.
 *
 * <p>Note: Methods in this class that rely on mutable state are explicitly sequential-only
 * and must not be used with parallel streams.
 */
public final class StreamUtils {

    private StreamUtils() {
       
    }

    /**
     * Returns a sequential stream that skips the first {@code n} elements for which the {@code predicate}
     * returns true. This is a stateful operation and therefore <b>must</b> be used with sequential streams.
     *
     * <p>Example usage:
     * <pre>{@code
     * StreamUtils.skipFirstNByPredicate(sourceStream, e -> "vampire".equals(e.getSpecies()), 10)
     *       .limit(500)
     *       .collect(Collectors.toList());
     * }</pre>
     *
     * @param source    source stream (will be consumed)
     * @param predicate predicate to test elements for skipping
     * @param n         number of matching elements to skip (non-negative)
     * @param <T>       element type
     * @return stream that skips first n matching elements (sequential)
     * @throws NullPointerException if source or predicate is null
     */
    public static <T> Stream<T> skipFirstNByPredicate(Stream<T> source,
                                                      java.util.function.Predicate<? super T> predicate,
                                                      int n) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(predicate, "predicate");
        final int threshold = Math.max(0, n);
        AtomicInteger counter = new AtomicInteger(0);
        return source.sequential().filter(element -> {
            if (predicate.test(element) && counter.get() < threshold) {
                counter.incrementAndGet();
                return false; 
            }
            return true; 
        });
    }
}
