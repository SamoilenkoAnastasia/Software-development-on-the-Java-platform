package com.kpi.stats;

import com.kpi.domain.MythicCreature;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector that aggregates attackPower values into AttackStats (min, max, mean, stddev).
 *
 * <p>Population standard deviation is used. If sample stddev is required, adjust variance formula accordingly.
 */
public final class AttackStatsCollector {

    private AttackStatsCollector() {
        
    }

    /**
     * Returns a Collector that collects MythicCreature into AttackStats using attackPower field.
     *
     * @return Collector
     */
    public static Collector<MythicCreature, ?, AttackStats> toAttackStats() {
        return Collector.of(Accumulator::new,
                Accumulator::accept,
                Accumulator::combine,
                Accumulator::toStats);
    }

    private static final class Accumulator implements BiConsumer<Accumulator, MythicCreature> {

        private long count;
        private double sum;
        private double sumSq;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;

        @Override
        public void accept(Accumulator acc, MythicCreature creature) {
            
        }

        void accept(MythicCreature creature) {
            double v = creature.getAttackPower();
            count++;
            sum += v;
            sumSq += v * v;
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        Accumulator combine(Accumulator other) {
            Accumulator r = new Accumulator();
            r.count = this.count + other.count;
            r.sum = this.sum + other.sum;
            r.sumSq = this.sumSq + other.sumSq;
            r.min = Math.min(this.min, other.min);
            r.max = Math.max(this.max, other.max);
            return r;
        }

        AttackStats toStats() {
            if (count == 0) {
                return new AttackStats(0, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
            }
            double mean = sum / count;
            double variance = (sumSq / count) - (mean * mean);
            if (variance < 0) {
                variance = 0;
            }
            double stdDev = Math.sqrt(variance);
            return new AttackStats(count, min, max, mean, stdDev);
        }
    }
}
