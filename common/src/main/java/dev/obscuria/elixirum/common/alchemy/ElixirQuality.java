package dev.obscuria.elixirum.common.alchemy;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Locale;

public enum ElixirQuality implements StringRepresentable {
    MIXTURE(Rarity.COMMON, false),
    PALE(Rarity.COMMON, false),
    CLOUDY(Rarity.COMMON, false),
    WEAK(Rarity.COMMON, false),
    MINOR(Rarity.COMMON, false),
    MODERATE(Rarity.COMMON, false),
    GRAND(Rarity.COMMON, false),
    INTENSE(Rarity.UNCOMMON, false),
    SUPREME(Rarity.RARE, false),
    LEGENDARY(Rarity.EPIC, true);

    public final Rarity rarity;
    public final boolean isFoil;

    ElixirQuality(Rarity rarity, boolean isFoil) {
        this.rarity = rarity;
        this.isFoil = isFoil;
    }

    public static Component makeElixirName(ItemStack stack, Component stackName) {
        var contents = ArsElixirumHelper.getElixirContents(stack);
        var quality = ElixirQuality.of(contents);
        if (quality == MIXTURE) return Component.literal("Suspicious Mixture");
        return Component.translatable(
                "elixirum.elixir_name_format",
                quality.displayName(),
                stackName,
                contents.displayName());
    }

    public Component displayName() {
        return Component.translatable("elixirum.quality.%s".formatted(getSerializedName()));
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static ElixirQuality of(ItemStack stack) {
        return of(ArsElixirumHelper.getElixirContents(stack));
    }

    public static ElixirQuality of(ElixirContents effects) {
        if (effects.isVoided()) return MIXTURE;
        return values()[Mth.clamp((int) Math.round(effects.quality()) / 10, 1, 9)];
    }
}