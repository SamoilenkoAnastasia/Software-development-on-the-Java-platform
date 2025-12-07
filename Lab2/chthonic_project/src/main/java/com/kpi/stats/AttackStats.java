package com.kpi.stats;

/**
 * Immutable DTO for attack-power statistics.
 */
public final class AttackStats {

    private final long count;
    private final double min;
    private final double max;
    private final double mean;
    private final double standardDeviation;

    public AttackStats(long count, double min, double max, double mean, double standardDeviation) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public long getCount() {
        return count;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    @Override
    public String toString() {
        return "AttackStats{" +
                "count=" + count +
                ", min=" + String.format("%.2f", min) +
                ", max=" + String.format("%.2f", max) +
                ", mean=" + String.format("%.2f", mean) +
                ", stdDev=" + String.format("%.2f", standardDeviation) +
                '}';
    }
}
