package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.api.events.AlchemyEvents;
import dev.obscuria.elixirum.helpers.MasteryHelper;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AlchemyMastery {

    public static final Codec<AlchemyMastery> CODEC;

    @Getter(AccessLevel.PRIVATE) private final Map<UUID, Integer> xpByRecipe;
    @Getter(AccessLevel.PUBLIC) private int level;
    @Getter(AccessLevel.PUBLIC) private int xp;

    public static AlchemyMastery empty() {
        return new AlchemyMastery(new HashMap<>(), 1, 0);
    }

    public AlchemyMastery(Map<UUID, Integer> xpByRecipe, int level, int xp) {
        this.xpByRecipe = new HashMap<>(xpByRecipe);
        this.level = level;
        this.xp = xp;
    }

    public int getRecipeXp(UUID recipeUid) {
        return this.xpByRecipe.getOrDefault(recipeUid, 0);
    }

    public boolean grantXp(AlchemyProfile profile, UUID recipeUid, int amount) {
        if (amount <= 0) return false;
        int currentXp = getRecipeXp(recipeUid);
        if (currentXp >= 100) return false;
        var xpToAdd = Math.min(amount, 100 - currentXp);
        this._setRecipeXp(recipeUid, currentXp + xpToAdd);
        AlchemyEvents.MASTERY.onRecipeXpGrant(profile, this, recipeUid, xpToAdd);
        this.grantXp(profile, xpToAdd);
        return true;
    }

    public boolean grantXp(AlchemyProfile profile, int amount) {
        if (level >= MasteryHelper.MAX_MASTERY_LEVEL) return false;
        this.xp += amount;
        AlchemyEvents.MASTERY.onXpGrant(profile, this, amount);
        this.maybeLevelUp(profile);
        return true;
    }

    public void setLevel(AlchemyProfile profile, int level) {
        if (!_setLevel(level)) return;
        AlchemyEvents.MASTERY.onLevelSet(profile, this);
    }

    // Internal methods

    public void _addRecipeXp(UUID recipeUid, int value) {
        this._setRecipeXp(recipeUid, getRecipeXp(recipeUid) + value);
    }

    public void _setRecipeXp(UUID recipeUid, int value) {
        this.xpByRecipe.put(recipeUid, Mth.clamp(value, 0, 100));
    }

    public void _addXp(int value) {
        this._setXp(xp + value);
    }

    public void _setXp(int value) {
        this.xp = Mth.clamp(value, 0, Integer.MAX_VALUE);
    }

    public boolean _addLevels(int value) {
        return _setLevel(level + value);
    }

    public boolean _setLevel(int value) {
        var clampedLevel = Mth.clamp(value, 1, MasteryHelper.MAX_MASTERY_LEVEL);
        if (level == clampedLevel) return false;
        this.level = Mth.clamp(value, 1, MasteryHelper.MAX_MASTERY_LEVEL);
        return true;
    }

    private void maybeLevelUp(AlchemyProfile profile) {
        while (xp >= MasteryHelper.calculateXpForLevel(level + 1)) {
            this.xp -= MasteryHelper.calculateXpForLevel(level + 1);
            this.setLevel(profile, level + 1);
            AlchemyEvents.MASTERY.onLevelUp(profile, this);
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).optionalFieldOf("xp_by_recipe", Map.of()).forGetter(AlchemyMastery::getXpByRecipe),
                Codec.INT.optionalFieldOf("level", 1).forGetter(AlchemyMastery::getLevel),
                Codec.INT.optionalFieldOf("xp", 0).forGetter(AlchemyMastery::getXp)
        ).apply(codec, AlchemyMastery::new));
    }
}
