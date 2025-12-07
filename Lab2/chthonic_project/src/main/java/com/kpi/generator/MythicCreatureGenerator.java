package com.kpi.generator;

import com.kpi.domain.MythicCreature;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Generator producing an infinite sequential Stream of MythicCreature objects.
 *
 * <p>Designed for realistic-ish values:
 * - first mention year between 800 and 2020
 * - attack power drawn from a skewed normal-like distribution
 */
public final class MythicCreatureGenerator {

    private static final List<String> NAMES = List.of(
            "Vesnik", "Mara", "Ghoul", "Rusalka", "Lisovyk", "Domovyk", "Baba Yaga",
            "Upyr", "Shadow", "Nightmare", "Chugaister", "Wendigo", "Incubus", "Succubus");

    private static final List<String> SPECIES = List.of(
            "vampire", "werewolf", "demon", "undead", "ghost", "spirit", "shadow");

    private MythicCreatureGenerator() {
        
    }

    /**
     * Infinite stream of MythicCreature.
     *
     * @return sequential Stream of MythicCreature
     */
    public static Stream<MythicCreature> infiniteStream() {
        Random random = ThreadLocalRandom.current();
        return Stream.generate(() -> randomBeing(random));
    }

    /**
     * Create a random creature using provided Random instance.
     *
     * @param random random source
     * @return new MythicCreature
     */
    public static MythicCreature randomBeing(Random random) {
        Objects.requireNonNull(random, "random");
        String species = SPECIES.get(random.nextInt(SPECIES.size()));
        String name = NAMES.get(random.nextInt(NAMES.size())) + " #" + (1 + random.nextInt(9999));

        LocalDate firstMention = randomFirstMentionDate(random);
        double attackPower = realisticAttackPower(random, species);

        return new MythicCreature(name, species, firstMention, attackPower);
    }

    private static LocalDate randomFirstMentionDate(Random random) {
        int startYear = 800; 
        int endYear = 2020;
        int year = startYear + random.nextInt(endYear - startYear + 1);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return LocalDate.of(year, month, day);
    }

    private static double realisticAttackPower(Random random, String species) {
        
        double base;
        switch (species) {
            case "vampire" -> base = 80.0;
            case "werewolf" -> base = 75.0;
            case "demon" -> base = 90.0;
            case "undead" -> base = 50.0;
            case "ghost" -> base = 40.0;
            case "spirit" -> base = 30.0;
            case "shadow" -> base = 60.0;
            default -> base = 50.0;
        }
        double noise = random.nextGaussian() * 15.0; 
        double result = Math.max(1.0, base + noise);
       
        if (random.nextDouble() < 0.02) {
            result *= 2 + random.nextDouble() * 3;
        }
        return result;
    }
}
