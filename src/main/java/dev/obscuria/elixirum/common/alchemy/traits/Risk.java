package dev.obscuria.elixirum.common.alchemy.traits;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.elixirum.helpers.IdRepresentable;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Risk implements Trait, IdRepresentable, StringRepresentable {
    PERFECT(-3, 0.0, Icon.RISK_LOW, Colors.rgbOf(0x9cc375)),
    STABLE(-2, 0.4, Icon.RISK_LOW, Colors.rgbOf(0xb5c376)),
    REFINED(-1, 0.8, Icon.RISK_LOW, Colors.rgbOf(0xc3b776)),
    BALANCED(0, 1.0, Icon.BALANCE, Colors.rgbOf(0xc39e76)),
    UNSTABLE(1, 1.2, Icon.RISK_HIGH, Colors.rgbOf(0xc38376)),
    VOLATILE(2, 1.6, Icon.RISK_HIGH, Colors.rgbOf(0xc37683)),
    CHAOTIC(3, 2.0, Icon.RISK_HIGH, Colors.rgbOf(0xc3769d));

    public static final Codec<Risk> CODEC;
    public static final Codec<Risk> STRING_CODEC;
    public static final Codec<Risk> ID_CODEC;
    public static final Component NAME;

    private final double sideEffectProbability;
    @Getter private final Component displayName;
    @Getter private final Component description;
    @Getter private final Icon icon;
    @Getter private final RGB color;
    @Getter private final int id;

    Risk(int id, double sideEffectProbability, Icon icon, RGB color) {
        this.displayName = Component.translatable("trait.elixirum.risk." + getSerializedName());
        this.description = Component.translatable("trait.elixirum.risk." + getSerializedName() + ".desc");
        this.sideEffectProbability = sideEffectProbability;
        this.icon = icon;
        this.color = color;
        this.id = id;
    }

    public double modifySideEffectProbability(double input) {
        return input * sideEffectProbability;
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
        ID_CODEC = IdRepresentable.fromEnum(Risk.values(), BALANCED);
        STRING_CODEC = StringRepresentable.fromEnum(Risk::values);
        CODEC = CodecHelper.alternative(ID_CODEC, STRING_CODEC);
        NAME = Component.translatable("trait.elixirum.risk");
    }
}