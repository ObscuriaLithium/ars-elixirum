package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record Essence(
        Aspect aspect,
        Holder<MobEffect> effect,
        int maxAmplifier,
        int maxDuration,
        int minQuality,
        RGB color
) {

    public static final Codec<Essence> CODEC;

    private static final double MAX_WEIGHT = 100.0;

    @SuppressWarnings("all")
    public static Essence create(
            Optional<Aspect> aspect,
            Holder<MobEffect> effect,
            int maxAmplifier,
            int maxDuration,
            int minQuality
    ) {
        return new Essence(
                aspect.orElseGet(() -> Aspect.select(effect)),
                effect, maxAmplifier, maxDuration, minQuality,
                Colors.rgbOf(effect.value().getColor()));
    }

    public int amplifierByWeight(double weight) {
        return linearValueByWeight(weight, maxAmplifier);
    }

    public int durationByWeight(double weight) {
        return linearValueByWeight(weight, maxDuration);
    }

    public double weightByAmplifier(int amplifier) {
        return linearWeightByValue(amplifier, maxAmplifier);
    }

    public double weightByDuration(int duration) {
        return linearWeightByValue(duration, maxDuration);
    }

    public Component displayName() {
        return effect.value().getDisplayName();
    }

    private static int quadValueByWeight(double weight, int maxValue) {
        if (weight >= MAX_WEIGHT) return maxValue;
        if (weight <= 0) return 0;
        final var ratio = weight / MAX_WEIGHT;
        return (int) (maxValue * (ratio * ratio));
    }

    private static double quadWeightByValue(int value, int maxValue) {
        if (value <= 0) return 0.0;
        if (value >= maxValue) return MAX_WEIGHT;
        final var ratio = Math.sqrt((double) value / maxValue);
        return MAX_WEIGHT * ratio + 0.001;
    }

    private static int linearValueByWeight(double weight, int maxValue) {
        if (weight >= MAX_WEIGHT) return maxValue;
        if (weight <= 0) return 0;
        final double ratio = weight / MAX_WEIGHT;
        return (int) (maxValue * ratio);
    }

    private static double linearWeightByValue(int value, int maxValue) {
        if (value <= 0) return 0.0;
        if (value >= maxValue) return MAX_WEIGHT;
        final double ratio = (double) value / maxValue;
        return MAX_WEIGHT * ratio;
    }

    private Optional<Aspect> optionalAspect() {
        return Optional.of(aspect);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.optionalFieldOf("aspect").forGetter(Essence::optionalAspect),
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(Essence::effect),
                Codec.INT.fieldOf("max_amplifier").forGetter(Essence::maxAmplifier),
                Codec.INT.fieldOf("max_duration").forGetter(Essence::maxDuration),
                Codec.INT.fieldOf("min_quality").forGetter(Essence::minQuality)
        ).apply(codec, Essence::create));
    }
}
