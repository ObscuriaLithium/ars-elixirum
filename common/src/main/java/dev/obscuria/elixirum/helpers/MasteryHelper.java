package dev.obscuria.elixirum.helpers;

import net.minecraft.util.Mth;

public final class MasteryHelper {

    public static final int MAX_MASTERY_LEVEL = 9999;
    public static final int XP_CURSE_LEVELS = 100;
    public static final int XP_CURVE_TOTAL = 2000;
    public static final int INITIAL_LEVEL_XP = 3;
    public static final double XP_GROWTH_RATE;

    public static int calculateXpForLevel(int level) {
        final var clampedLevel = Mth.clamp(level, 1, XP_CURSE_LEVELS);
        return (int) (INITIAL_LEVEL_XP * Math.pow(XP_GROWTH_RATE, clampedLevel));
    }

    private static double calculateXpGrowthRate() {
        var left = 1.0;
        var right = 2.0;

        for (var i = 0; i < 100; i++) {
            var mid = (left + right) / 2.0;
            var sum = Math.abs(mid - 1.0) < 1e-9
                    ? INITIAL_LEVEL_XP * XP_CURSE_LEVELS
                    : INITIAL_LEVEL_XP * ((Math.pow(mid, XP_CURSE_LEVELS) - 1.0) / (mid - 1.0));

            if (sum < XP_CURVE_TOTAL) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return (left + right) / 2.0;
    }

    static {
        XP_GROWTH_RATE = calculateXpGrowthRate();
    }
}
