package dev.obscuria.elixirum.common.alchemy.brewing;

import lombok.Getter;

public enum ManifestationTier {
    DOMINANT(true, 1.2),
    PRIMARY(true, 1.0),
    TRACE(false, 0.2);

    @Getter private final boolean guaranteed;
    @Getter private final double weightFactor;

    ManifestationTier(boolean guaranteed, double weightFactor) {
        this.guaranteed = guaranteed;
        this.weightFactor = weightFactor;
    }

    public double apply(double weight) {
        return weight * weightFactor;
    }
}
