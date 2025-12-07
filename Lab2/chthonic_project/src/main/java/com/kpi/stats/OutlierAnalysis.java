package com.kpi.stats;

import com.kpi.domain.MythicCreature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Outlier detection using Inter-Quartile Range (IQR) method for attackPower.
 */
public final class OutlierAnalysis {

    private OutlierAnalysis() {
        // utility only
    }

    /**
     * Analyze attackPower values and return map with counts: {"data" -> non-outlier count, "outliers" -> outlier count}
     *
     * @param creatures list of creatures
     * @return map with data/outliers counts
     */
    public static Map<String, Long> analyzeAttackPowers(List<MythicCreature> creatures) {
        List<Double> values = creatures.stream()
                .map(MythicCreature::getAttackPower)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        if (values.isEmpty()) {
            return Map.of("data", 0L, "outliers", 0L);
        }

        double q1 = percentile(values, 25.0);
        double q3 = percentile(values, 75.0);
        double iqr = q3 - q1;

        double lower = q1 - 1.5 * iqr;
        double upper = q3 + 1.5 * iqr;

        long outliers = values.stream().filter(v -> v < lower || v > upper).count();
        long data = values.size() - outliers;

        return Map.of("data", data, "outliers", outliers);
    }

    /**
     * Percentile implementation using linear interpolation between nearest ranks.
     *
     * @param sortedValues strictly non-null list sorted ascending
     * @param percentile   between 0..100
     * @return percentile value
     */
    private static double percentile(List<Double> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return Double.NaN;
        }
        if (percentile <= 0.0) {
            return sortedValues.get(0);
        }
        if (percentile >= 100.0) {
            return sortedValues.get(sortedValues.size() - 1);
        }
        double index = percentile / 100.0 * (sortedValues.size() - 1);
        int lo = (int) Math.floor(index);
        int hi = (int) Math.ceil(index);
        double frac = index - lo;
        double loVal = sortedValues.get(lo);
        double hiVal = sortedValues.get(hi);
        return loVal + (hiVal - loVal) * frac;
    }
}
