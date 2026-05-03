package dev.obscuria.elixirum.common.alchemy.brewing;

public enum Diversity {
    DISTINCT(1.0),
    REPEATED(0.6),
    UNIFORM(0.2);

    private final double multiplier;

    Diversity(double multiplier) {
        this.multiplier = multiplier;
    }

    public double apply(double weight) {
        return weight * multiplier;
    }
}
