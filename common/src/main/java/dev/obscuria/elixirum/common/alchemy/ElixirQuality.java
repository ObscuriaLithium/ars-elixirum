package dev.obscuria.elixirum.common.alchemy;

import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.helpers.ContentsHelper;
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
        return fromContents(ContentsHelper.elixir(stack));
    }

    public static ElixirQuality fromContents(ElixirContents effects) {
        if (effects.isVoided()) return MIXTURE;
        var index = (int) (Math.round(effects.quality()) / 10) - 1;
        return switch (index) {
            case 0 -> PALE;
            case 1 -> CLOUDY;
            case 2 -> WEAK;
            case 3 -> MINOR;
            case 4 -> MODERATE;
            case 5 -> GRAND;
            case 6 -> INTENSE;
            case 7 -> SUPREME;
            default -> LEGENDARY;
        };
    }
}