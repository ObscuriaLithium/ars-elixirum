package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.api.codex.profile.AlchemyMastery;
import dev.obscuria.elixirum.api.events.AlchemyEvents;
import dev.obscuria.elixirum.helpers.MasteryHelper;
import dev.obscuria.elixirum.server.VersionedCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class _AlchemyMastery implements AlchemyMastery {

    public static final Codec<AlchemyMastery> CODEC;
    public static final int MAX_RECIPE_XP = 30;

    private final Map<UUID, Integer> xpByRecipe;
    private int level;
    private int xp;

    public _AlchemyMastery(Map<UUID, Integer> xpByRecipe, int level, int xp) {
        this.xpByRecipe = new HashMap<>(xpByRecipe);
        this.level = level;
        this.xp = xp;
    }

    // XP

    @Override
    public int xp() {
        return this.xp;
    }

    @Override
    public int xpForNextLevel() {
        return MasteryHelper.calculateXpForLevel(level + 1) - xp;
    }

    @Override
    public boolean setXp(AlchemyProfile profile, int value) {
        return setXpInternal(profile, value);
    }

    @Override
    public boolean setXpNoEvent(int value) {
        return setXpInternal(null, value);
    }

    // RECIPE XP

    @Override
    public int recipeXp(UUID recipeUid) {
        return this.xpByRecipe.getOrDefault(recipeUid, 0);
    }

    @Override
    public boolean setRecipeXp(AlchemyProfile profile, UUID recipeUid, int value) {
        return setRecipeXpInternal(profile, recipeUid, value);
    }

    @Override
    public boolean setRecipeXpNoEvent(UUID recipeUid, int amount) {
        return setRecipeXpInternal(null, recipeUid, amount);
    }

    // LEVEL

    @Override
    public int level() {
        return this.level;
    }

    @Override
    public boolean setLevel(AlchemyProfile profile, int value) {
        return setLevelInternal(profile, value);
    }

    @Override
    public boolean setLevelNoEvent(int value) {
        return setLevelInternal(null, value);
    }

    // INTERNAL

    private boolean setXpInternal(@Nullable AlchemyProfile profile, int value) {
        var clampedXp = Mth.clamp(value, 0, Integer.MAX_VALUE);
        if (xp == clampedXp) return false;
        this.xp = clampedXp;
        if (profile != null) AlchemyEvents.MASTERY.onXpChanged(profile, this);
        maybeLevelUp(profile);
        return true;
    }

    private boolean setRecipeXpInternal(@Nullable AlchemyProfile profile, UUID recipeUid, int value) {
        var clampedXp = Mth.clamp(value, 0, MAX_RECIPE_XP);
        var currentXp = recipeXp(recipeUid);
        if (currentXp == clampedXp) return false;
        this.xpByRecipe.put(recipeUid, clampedXp);
        if (profile != null) AlchemyEvents.MASTERY.onRecipeXpChanged(profile, this, recipeUid);
        setXpInternal(profile, xp + (clampedXp - currentXp));
        return true;
    }

    private boolean setLevelInternal(@Nullable AlchemyProfile profile, int value) {
        var clampedLevel = Mth.clamp(value, 1, MasteryHelper.MAX_MASTERY_LEVEL);
        if (level == clampedLevel) return false;
        this.level = clampedLevel;
        if (profile != null) AlchemyEvents.MASTERY.onLevelChanged(profile, this);
        return true;
    }

    private void maybeLevelUp(@Nullable AlchemyProfile profile) {
        while (xp >= MasteryHelper.calculateXpForLevel(level + 1)) {
            this.xp -= MasteryHelper.calculateXpForLevel(level + 1);
            if (!setLevelInternal(profile, level + 1)) break;
            if (profile != null) AlchemyEvents.MASTERY.onLevelUp(profile, this);
        }
    }

    private static Map<UUID, Integer> getXpByRecipeMap(AlchemyMastery mastery) {
        return mastery instanceof _AlchemyMastery typed ? typed.xpByRecipe : Map.of();
    }

    static {
        CODEC = VersionedCodec.<AlchemyMastery>of(RecordCodecBuilder.create(codec -> codec.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).optionalFieldOf("xp_by_recipe", Map.of()).forGetter(_AlchemyMastery::getXpByRecipeMap),
                Codec.INT.optionalFieldOf("level", 1).forGetter(AlchemyMastery::level),
                Codec.INT.optionalFieldOf("xp", 0).forGetter(AlchemyMastery::xp)
        ).apply(codec, AlchemyMastery::create))).build();
    }
}
