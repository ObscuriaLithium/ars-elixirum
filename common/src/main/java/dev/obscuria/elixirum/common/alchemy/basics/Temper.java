package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Temper implements StringRepresentable {
    MAX_AMPLIFIER(-1.0),
    VERY_HIGH_AMPLIFIER(-0.8),
    HIGH_AMPLIFIER(-0.6),
    MODERATE_AMPLIFIER(-0.4),
    SLIGHTLY_AMPLIFIER(-0.2),
    BALANCED(0.0),
    SLIGHTLY_DURATION(0.2),
    MODERATE_DURATION(0.4),
    HIGH_DURATION(0.6),
    VERY_HIGH_DURATION(0.8),
    MAX_DURATION(1.0);

    public static final Codec<Temper> CODEC = StringRepresentable.fromEnum(Temper::values);
    public final double value;

    Temper(double value) {
        this.value = value;
    }

    public static Temper byValue(double input) {
        var closest = Temper.values()[0];
        var minDiff = Math.abs(input - closest.value);

        for (var temper : Temper.values()) {
            var diff = Math.abs(input - temper.value);
            if (diff < minDiff) {
                minDiff = diff;
                closest = temper;
            }
        }

        return closest;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
