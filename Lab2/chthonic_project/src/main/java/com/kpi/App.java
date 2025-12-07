package com.kpi;

import com.kpi.domain.MythicCreature;
import com.kpi.gather.ChthonicGatherers; 
import com.kpi.generator.MythicCreatureGenerator;
import com.kpi.stats.AttackStats;
import com.kpi.stats.AttackStatsCollector;
import com.kpi.stats.OutlierAnalysis;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Main application class. Executes the entire chthonic creature analysis pipeline.
 *
 * <p>Usage:
 * java -cp target/... com.kpi.App [skipN] [speciesToSkip] [minYears] [maxYears]
 */
public final class App {

    private App() {
       
    }

    public static void main(String[] args) {
        
        int skipN = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        String speciesToSkip = args.length > 1 ? args[1] : "vampire";
        int minYears = args.length > 2 ? Integer.parseInt(args[2]) : 50;
        int maxYears = args.length > 3 ? Integer.parseInt(args[3]) : 500;

        LocalDate today = LocalDate.now();

        Predicate<MythicCreature> predicate = c -> speciesToSkip.equalsIgnoreCase(c.getSpecies());

        List<MythicCreature> creatures = MythicCreatureGenerator.infiniteStream()
                .gather(ChthonicGatherers.skipFirstNByPredicate(predicate, skipN))
                .limit(500)
                .collect(Collectors.toList());

        List<MythicCreature> filtered = creatures.stream()
                .filter(c -> {
                    int years = c.getYearsSinceFirstMention(today); 
                    return years >= minYears && years <= maxYears;
                })
                .collect(Collectors.toList());

        Map<String, List<MythicCreature>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(MythicCreature::getSpecies)); 

        System.out.println("=== Grouped by species (after years filter) ===");
        grouped.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> System.out.printf("%s: %d%n", e.getKey(), e.getValue().size()));

        AttackStats stats = filtered.stream().collect(AttackStatsCollector.toAttackStats());

        System.out.println();
        System.out.println("=== Attack power statistics (Field G) ===");
        System.out.println(stats); 

        Map<String, Long> outliers = OutlierAnalysis.analyzeAttackPowers(filtered);

        System.out.println();
        System.out.println("=== IQR outlier analysis (Field G) ===");
        System.out.println(outliers);
    }
}