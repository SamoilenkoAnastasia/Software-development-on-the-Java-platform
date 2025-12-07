package com.kpi.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Domain class representing a mythic (chthonic) creature.
 *
 * <p>Fields:
 * <ul>
 * <li>name - creature name</li>
 * <li>species - type/species (e.g. vampire, demon)</li>
 * <li>firstMentionDate - date of first mention in literature</li>
 * <li>attackPower - attack strength (arbitrary units)</li>
 * </ul>
 */
public final class MythicCreature {

    private final String name;
    private final String species;
    private final LocalDate firstMentionDate;
    private final double attackPower;

    /**
     * Constructs a new MythicCreature.
     *
     * @param name             non-null name
     * @param species          non-null species
     * @param firstMentionDate non-null first mention date
     * @param attackPower      attack power (>= 0)
     */
    public MythicCreature(String name, String species, LocalDate firstMentionDate, double attackPower) {
        this.name = Objects.requireNonNull(name, "name");
        this.species = Objects.requireNonNull(species, "species");
        this.firstMentionDate = Objects.requireNonNull(firstMentionDate, "firstMentionDate");
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public LocalDate getFirstMentionDate() {
        return firstMentionDate;
    }

    public double getAttackPower() {
        return attackPower;
    }

    /**
     * Returns number of years between {@code firstMentionDate} and {@code today}.
     * If {@code today} is before {@code firstMentionDate}, returns 0.
     *
     * @param today reference date
     * @return years since first mention (Field B)
     */
    public int getYearsSinceFirstMention(LocalDate today) {
        Objects.requireNonNull(today);
        if (today.isBefore(firstMentionDate)) {
            return 0;
        }
        return Period.between(firstMentionDate, today).getYears();
    }

    /**
     * Returns a string representation of the object.
     * Attack power (Field G) is formatted to two decimal places.
     */
    @Override
    public String toString() {
       
        return "MythicCreature{" +
                "name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", firstMentionDate=" + firstMentionDate +
                ", attackPower=" + String.format("%.2f", attackPower) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MythicCreature that = (MythicCreature) o;

        if (Double.compare(that.attackPower, attackPower) != 0) return false;
        if (!name.equals(that.name)) return false;
        if (!species.equals(that.species)) return false;
        return firstMentionDate.equals(that.firstMentionDate);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;

        result = name.hashCode();
        result = 31 * result + species.hashCode();
        result = 31 * result + firstMentionDate.hashCode();
      
        temp = Double.doubleToLongBits(attackPower);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}