package dev.obscuria.elixirum.common.alchemy.brewing;

public enum Concordance {
    PERFECT(1.4),
    PARTIAL(1.2),
    NONE(1.0);

    private final double weightMultiplier;

    Concordance(double weightMultiplier) {
        this.weightMultiplier = weightMultiplier;
    }

    public double apply(double weight) {
        return weight * weightMultiplier;
    }
}
