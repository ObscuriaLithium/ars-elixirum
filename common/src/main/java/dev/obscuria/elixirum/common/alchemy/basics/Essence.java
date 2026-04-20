package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;

public record Essence(
        Aspect aspect,
        Holder<MobEffect> effect,
        int maxAmplifier,
        int maxDuration,
        int minQuality
) {

    public static final Codec<Essence> CODEC;
    public static final Essence EMPTY;

    public Essence(Holder<MobEffect> effect, int maxAmplifier, int maxDuration, int minQuality) {
        this(Aspect.select(effect), effect, maxAmplifier, maxDuration, minQuality);
    }

    private static final double MAX_WEIGHT = 100.0;

    public boolean isEmpty() {
        return effect == null;
    }

    public Component displayName() {
        return effect.value().getDisplayName();
    }

    public RGB color() {
        return Colors.rgbOf(effect.value().getColor());
    }

    public int unpackAmplifier(double weight) {
        return linearValueByWeight(weight, maxAmplifier);
    }

    public int unpackDuration(double weight) {
        return linearValueByWeight(weight, maxDuration);
    }

    public double packAmplifier(int amplifier) {
        return linearWeightByValue(amplifier, maxAmplifier);
    }

    public double packDuration(int duration) {
        return linearWeightByValue(duration, maxDuration);
    }

    //TODO
    public EssenceHolder asHolder() {
        return Alchemy.guess().essences().asHolder(this);
    }

    public static int quadValueByWeight(double weight, int maxValue) {
        if (weight >= MAX_WEIGHT) return maxValue;
        if (weight <= 0) return 0;
        final var ratio = weight / MAX_WEIGHT;
        return (int) (maxValue * (ratio * ratio));
    }

    public static double quadWeightByValue(int value, int maxValue) {
        if (value <= 0) return 0.0;
        if (value >= maxValue) return MAX_WEIGHT;
        final var ratio = Math.sqrt((double) value / maxValue);
        return MAX_WEIGHT * ratio + 0.001;
    }

    public static int linearValueByWeight(double weight, int maxValue) {
        if (weight >= MAX_WEIGHT) return maxValue;
        if (weight <= 0) return 0;
        final double ratio = weight / MAX_WEIGHT;
        return (int) (maxValue * ratio);
    }

    public static double linearWeightByValue(int value, int maxValue) {
        if (value <= 0) return 0.0;
        if (value >= maxValue) return MAX_WEIGHT;
        final double ratio = (double) value / maxValue;
        return MAX_WEIGHT * ratio;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(Essence::effect),
                Codec.INT.fieldOf("max_amplifier").forGetter(Essence::maxAmplifier),
                Codec.INT.fieldOf("max_duration").forGetter(Essence::maxDuration),
                Codec.INT.fieldOf("min_quality").forGetter(Essence::minQuality)
        ).apply(codec, Essence::new));
        EMPTY = new Essence(Aspect.NONE, null, 0, 0, 0);
    }
}
