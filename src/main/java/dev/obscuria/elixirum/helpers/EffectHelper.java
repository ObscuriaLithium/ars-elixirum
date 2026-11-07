package dev.obscuria.elixirum.helpers;

import net.minecraft.util.Mth;

public final class EffectHelper {

    public static double amplifierFactor(double temper) {
        return Mth.clampedMap(temper, -1f, 1f, 0.1f, 1f);
    }

    public static double durationFactor(double temper) {
        return Mth.clampedMap(temper, 1f, -1f, 0.1f, 1f);
    }
}
