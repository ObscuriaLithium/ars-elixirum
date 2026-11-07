package dev.obscuria.elixirum.api.codex.profile;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.common.alchemy.codex.components._AlchemyMastery;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AlchemyMastery {

    static Codec<AlchemyMastery> codec() {
        return _AlchemyMastery.CODEC;
    }

    static AlchemyMastery empty() {
        return new _AlchemyMastery(new HashMap<>(), 1, 0);
    }

    static AlchemyMastery create(Map<UUID, Integer> xpByRecipe, int level, int xp) {
        return new _AlchemyMastery(xpByRecipe, level, xp);
    }

    // XP

    int xp();

    int xpForNextLevel();

    boolean setXp(AlchemyProfile profile, int value);

    boolean setXpNoEvent(int value);

    default boolean addXp(AlchemyProfile profile, int amount) {
        return setXp(profile, xp() + amount);
    }

    default boolean addXpNoEvent(int amount) {
        return setXpNoEvent(xp() + amount);
    }

    // RECIPE XP

    int recipeXp(UUID recipeUid);

    boolean setRecipeXp(AlchemyProfile profile, UUID recipeUid, int value);

    boolean setRecipeXpNoEvent(UUID recipeUid, int value);

    default boolean addRecipeXp(AlchemyProfile profile, UUID recipeUid, int amount) {
        return setRecipeXp(profile, recipeUid, recipeXp(recipeUid) + amount);
    }

    default boolean addRecipeXpNoEvent(UUID recipeUid, int amount) {
        return setRecipeXpNoEvent(recipeUid, recipeXp(recipeUid) + amount);
    }

    // LEVELS

    int level();

    boolean setLevel(AlchemyProfile profile, int value);

    boolean setLevelNoEvent(int value);

    default boolean addLevels(AlchemyProfile profile, int amount) {
        return setLevel(profile, level() + amount);
    }

    default boolean addLevelsNoEvent(int amount) {
        return setLevelNoEvent(level() + amount);
    }
}
