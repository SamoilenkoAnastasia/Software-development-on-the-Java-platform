package com.kpi.gather;

import com.kpi.domain.MythicCreature;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

/**
 * Custom Stream Gatherer implementations for the chthonic project.
 *
 * <p>Requires Java 22 or newer for the Gatherer API.
 */
public final class ChthonicGatherers {

    private ChthonicGatherers() {
 
    }

    /**
     * Creates a Gatherer that skips the first 'n' elements matching the predicate (Field A check).
     *
     * <p>Uses Gatherer.ofSequential because the operation is stateful and must run sequentially.
     *
     * @param predicate predicate that defines which elements to skip
     * @param n number of matching elements to skip
     * @return a Gatherer that performs the skipping logic
     */
    public static Gatherer<MythicCreature, AtomicInteger, MythicCreature> skipFirstNByPredicate(
            Predicate<? super MythicCreature> predicate,
            int n) {

        final int threshold = Math.max(0, n);

        return Gatherer.ofSequential(
                AtomicInteger::new,

                (state, element, downstream) -> {

                    if (predicate.test(element) && state.get() < threshold) {
                        state.incrementAndGet();
                        return true; 
                    }
                    downstream.push(element);
                    return true;
                }
        );
    }
}
