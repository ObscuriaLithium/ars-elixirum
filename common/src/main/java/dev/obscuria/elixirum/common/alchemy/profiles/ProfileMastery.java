package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.world.AtomicCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public record ProfileMastery(
        AtomicInteger masteryLevel,
        AtomicInteger masteryXp,
        Map<UUID, Integer> recipeToMastery,
        AtomicReference<Listener> listener
) {

    public static final int MAX_MASTERY_LEVEL = 9999;
    public static final int XP_CURSE_LEVELS = 100;
    public static final int XP_CURVE_TOTAL = 2000;
    public static final int INITIAL_LEVEL_XP = 3;
    public static final double XP_GROWTH_RATE;

    public static final Codec<ProfileMastery> CODEC;

    public static ProfileMastery empty() {
        return new ProfileMastery(
                new AtomicInteger(1),
                new AtomicInteger(0),
                new HashMap<>(),
                new AtomicReference<>(new Listener() {}));
    }

    public int ofRecipe(UUID recipe) {
        return recipeToMastery.getOrDefault(recipe, 0);
    }

    public int xpForNextLevel() {
        return xpForLevel(masteryLevel.get() + 1);
    }

    public void grandXp(UUID recipe, int amount) {
        if (amount <= 0) return;
        final var currentXp = ofRecipe(recipe);
        if (currentXp >= 100) return;
        final var xpToAdd = Math.min(amount, 100 - currentXp);
        this.recipeToMastery.put(recipe, currentXp + xpToAdd);
        this.listener.get().onRecipeXp(this, recipe, xpToAdd);
        this.listener.get().onChanged(this);
        grandXp(xpToAdd);
    }

    public void grandXp(int amount) {
        if (masteryLevel.get() < MAX_MASTERY_LEVEL) {
            this.masteryXp.addAndGet(amount);
            this.listener.get().onGenericXp(this, amount);
            this.listener.get().onChanged(this);
            this.maybeLevelUp();
        }
    }

    public boolean changeLevel(int level) {
        final var clamped = Mth.clamp(level, 1, MAX_MASTERY_LEVEL);
        if (masteryLevel.get() == clamped) return false;
        this.masteryLevel.set(clamped);
        this.listener.get().onLevelUp(this);
        this.listener.get().onChanged(this);
        return true;
    }

    public void updateFrom(ProfileMastery other) {
        this.masteryLevel.set(other.masteryLevel.get());
        this.masteryXp.set(other.masteryXp.get());
        this.recipeToMastery.clear();
        this.recipeToMastery.putAll(other.recipeToMastery);
    }

    private void maybeLevelUp() {
        while (masteryXp.get() >= xpForNextLevel()) {
            masteryXp.addAndGet(-xpForNextLevel());
            changeLevel(masteryLevel.get() + 1);
        }
    }

    public static int xpForLevel(int level) {
        final var clampedLevel = Mth.clamp(level, 1, XP_CURSE_LEVELS);
        return (int) (INITIAL_LEVEL_XP * Math.pow(XP_GROWTH_RATE, clampedLevel));
    }

    private static ProfileMastery load(
            AtomicInteger masteryLevel,
            AtomicInteger masteryExp,
            Map<UUID, Integer> recipeToMastery) {
        return new ProfileMastery(
                masteryLevel,
                masteryExp,
                new HashMap<>(recipeToMastery),
                new AtomicReference<>(new Listener() {}));
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

    public interface Listener {

        default void onChanged(ProfileMastery mastery) {}

        default void onRecipeXp(ProfileMastery mastery, UUID recipe, int amount) {}

        default void onGenericXp(ProfileMastery mastery, int amount) {}

        default void onLevelUp(ProfileMastery mastery) {}
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AtomicCodecs.INT.fieldOf("mastery_level").forGetter(ProfileMastery::masteryLevel),
                AtomicCodecs.INT.fieldOf("mastery_xp").forGetter(ProfileMastery::masteryXp),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("recipe_to_mastery").forGetter(ProfileMastery::recipeToMastery)
        ).apply(codec, ProfileMastery::load));
        XP_GROWTH_RATE = calculateXpGrowthRate();
    }
}