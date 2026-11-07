package dev.obscuria.elixirum.common.alchemy;

import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import lombok.Getter;
import net.minecraft.network.chat.Component;
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

    @Getter private final Rarity rarity;
    @Getter private final boolean isFoil;
    @Getter private final Component displayName;
    @Getter private final Component assessment;

    ElixirQuality(Rarity rarity, boolean isFoil) {
        this.rarity = rarity;
        this.isFoil = isFoil;
        this.displayName = Component.translatable("quality.elixirum." + getSerializedName());
        this.assessment = Component.translatable("assessment.elixirum." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static ElixirQuality fromStack(ItemStack stack) {
        return fromContents(ArsElixirumAPI.getElixirContents(stack));
    }

    public static ElixirQuality fromContents(ElixirContents effects) {
        if (effects.isVoided()) return MIXTURE;
        return fromQuality(effects.quality());
    }

    public static ElixirQuality fromQuality(double quality) {
        var index = (int) (quality / 10);
        return switch (index) {
            case 0, 1, 2 -> PALE;
            case 3 -> CLOUDY;
            case 4 -> WEAK;
            case 5 -> MINOR;
            case 6 -> MODERATE;
            case 7 -> GRAND;
            case 8 -> INTENSE;
            case 9 -> SUPREME;
            default -> LEGENDARY;
        };
    }
}