package dev.obscuria.elixirum.common.alchemy.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

public record DirectEffectProvider(
        EssenceHolder holder,
        int amplifier,
        int duration
) implements EffectProvider {

    public static final Codec<DirectEffectProvider> CODEC;

    @Override
    public Codec<DirectEffectProvider> codec() {
        return CODEC;
    }

    @Override
    public double quality() {
        final var packedAmplifier = holder.map(it -> it.weightByAmplifier(amplifier)).orElse(0.0);
        final var packedDuration = holder.map(it -> it.weightByDuration(duration)).orElse(0.0);
        return Mth.clamp((packedAmplifier + packedDuration) * 0.5, 0.0, 100.0);
    }

    @Override
    public EffectProvider splitBy(int amount) {
        final var newAmplifier = amplifier * (1.0 / amount);
        final var newDuration = duration * (1.0 / amount);
        return new DirectEffectProvider(holder, (int) newAmplifier, (int) newDuration);
    }

    @Override
    public EffectProvider scale(double factor) {
        return new DirectEffectProvider(holder, amplifier, (int) Math.ceil(duration * factor));
    }

    @Override
    public EffectProvider withWeight(double weight) {
        return new DirectEffectProvider(holder, amplifier, holder.map(it -> it.durationByWeight(weight)).orElse(0));
    }

    @Override
    public MobEffectInstance instantiate(double mastery, double immunity) {
        final var tickDuration = duration;
        final var scaledDuration = isBeneficial() ? tickDuration + 20.0 * mastery : tickDuration - 20.0 * immunity;
        final var actualDuration = Mth.clamp(scaledDuration, tickDuration * 0.5, tickDuration * 1.5);
        return new MobEffectInstance(mobEffect(), (int) actualDuration, amplifier);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                EssenceHolder.CODEC.fieldOf("essence").forGetter(DirectEffectProvider::holder),
                Codec.INT.fieldOf("amplifier").forGetter(DirectEffectProvider::amplifier),
                Codec.INT.fieldOf("duration").forGetter(DirectEffectProvider::duration)
        ).apply(codec, DirectEffectProvider::new));
    }
}
