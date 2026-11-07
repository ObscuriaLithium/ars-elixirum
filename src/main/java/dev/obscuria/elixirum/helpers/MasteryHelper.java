package dev.obscuria.elixirum.helpers;

import dev.obscuria.elixirum.api.codex.Alchemy;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class MasteryHelper {

    public static final int MAX_MASTERY_LEVEL = 9999;
    public static final int XP_CURSE_LEVELS = 100;
    public static final int XP_CURVE_TOTAL = 2000;
    public static final int INITIAL_LEVEL_XP = 3;
    public static final double XP_GROWTH_RATE;

    public static int levelOf(Player player) {
        return Alchemy.get(player.level()).profileOf(player).mastery().level();
    }

    public static int xpOf(Player player) {
        return Alchemy.get(player.level()).profileOf(player).mastery().xp();
    }

    public static boolean addXp(Player player, int amount) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().addXp(profile, amount);
    }

    public static boolean setXp(Player player, int value) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().setXp(profile, value);
    }

    public static boolean addRecipeXp(Player player, UUID recipeUid, int amount) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().addRecipeXp(profile, recipeUid, amount);
    }

    public static boolean setRecipeXp(Player player, UUID recipeUid, int value) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().setRecipeXp(profile, recipeUid, value);
    }

    public static boolean addLevels(Player player, int amount) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().addLevels(profile, amount);
    }

    public static boolean setLevel(Player player, int value) {
        var profile = Alchemy.get(player.level()).profileOf(player);
        return profile.mastery().setLevel(profile, value);
    }

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
