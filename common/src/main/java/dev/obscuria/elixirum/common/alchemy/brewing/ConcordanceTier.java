package dev.obscuria.elixirum.common.alchemy.brewing;

public enum ConcordanceTier {
    PERFECT(1.30),
    PARTIAL(1.15),
    NONE(1.0);

    private final double weightMultiplier;

    ConcordanceTier(double weightMultiplier) {
        this.weightMultiplier = weightMultiplier;
    }

    public double apply(double weight) {
        return weight * weightMultiplier;
    }
}
