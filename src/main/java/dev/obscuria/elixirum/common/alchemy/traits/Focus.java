package dev.obscuria.elixirum.common.alchemy.traits;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.elixirum.helpers.IdRepresentable;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Focus implements Trait, IdRepresentable, StringRepresentable {
    MAX_DURATION(-5, -1.0, Icon.FOCUS_DURATION, Palette.FOCUS_BLUE),
    VERY_HIGH_DURATION(-4, -0.8, Icon.FOCUS_DURATION, Palette.FOCUS_BLUE),
    HIGH_DURATION(-3, -0.6, Icon.FOCUS_DURATION, Palette.FOCUS_BLUE),
    MODERATE_DURATION(-2, -0.4, Icon.FOCUS_DURATION, Palette.FOCUS_BLUE),
    SLIGHTLY_DURATION(-1, -0.2, Icon.FOCUS_DURATION, Palette.FOCUS_BLUE),
    BALANCED(0, 0.0, Icon.BALANCE, Palette.FOCUS_GREEN),
    SLIGHTLY_POTENCY(1, 0.2, Icon.FOCUS_POTENCY, Palette.FOCUS_YELLOW),
    MODERATE_POTENCY(2, 0.4, Icon.FOCUS_POTENCY, Palette.FOCUS_YELLOW),
    HIGH_POTENCY(3, 0.6, Icon.FOCUS_POTENCY, Palette.FOCUS_YELLOW),
    VERY_HIGH_POTENCY(4, 0.8, Icon.FOCUS_POTENCY, Palette.FOCUS_YELLOW),
    MAX_POTENCY(5, 1.0, Icon.FOCUS_POTENCY, Palette.FOCUS_YELLOW);

    public static final Codec<Focus> CODEC;
    public static final Codec<Focus> ID_CODEC;
    public static final Codec<Focus> STRING_CODEC;
    public static final Component NAME;

    public final double value;

    @Getter private final Component displayName;
    @Getter private final Component description;
    @Getter private final Icon icon;
    @Getter private final RGB color;
    @Getter private final int id;

    Focus(int id, double value, Icon icon, RGB color) {
        this.displayName = Component.translatable("trait.elixirum.focus." + getSerializedName());
        this.description = Component.translatable("trait.elixirum.focus." + getSerializedName() + ".desc");
        this.icon = icon;
        this.color = color;
        this.value = value;
        this.id = id;
    }

    public static Focus byValue(double input) {
        var closest = Focus.values()[0];
        var minDiff = Math.abs(input - closest.value);

        for (var temper : Focus.values()) {
            var diff = Math.abs(input - temper.value);
            if (diff < minDiff) {
                minDiff = diff;
                closest = temper;
            }
        }

        return closest;
    }

    @Override
    public Component getCategoryName() {
        return NAME;
    }

    @Override
    public float getProgressShift() {
        var center = values().length / 2;
        return (ordinal() - center) / (float) center;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    static {
        ID_CODEC = IdRepresentable.fromEnum(Focus.values(), BALANCED);
        STRING_CODEC = StringRepresentable.fromEnum(Focus::values);
        CODEC = CodecHelper.alternative(ID_CODEC, STRING_CODEC);
        NAME = Component.translatable("trait.elixirum.focus");
    }
}
