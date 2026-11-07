package dev.obscuria.elixirum.common.alchemy.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.helpers.EffectHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

public record PackedEffectProvider(
        EssenceHolder holder,
        double weight,
        double focus
) implements EffectProvider {

    public static final Codec<PackedEffectProvider> CODEC;

    @Override
    public Codec<PackedEffectProvider> codec() {
        return CODEC;
    }

    @Override
    public double quality() {
        return weight;
    }

    @Override
    public int amplifier() {
        if (!holder.isBound()) return 0;
        return holder.value().amplifierByWeight(weight * EffectHelper.amplifierFactor(focus));
    }

    @Override
    public int duration() {
        if (!holder.isBound()) return 0;
        return holder.value().durationByWeight(weight * EffectHelper.durationFactor(focus));
    }

    @Override
    public EffectProvider splitBy(int amount) {
        return new PackedEffectProvider(holder, weight * (1.0 / amount), focus);
    }

    @Override
    public EffectProvider scale(double factor) {
        return new PackedEffectProvider(holder, weight * factor, focus);
    }

    @Override
    public EffectProvider withWeight(double weight) {
        return new PackedEffectProvider(holder, weight, focus);
    }

    @Override
    public MobEffectInstance instantiate(double mastery, double immunity) {
        final var tickDuration = duration();
        final var scaledDuration = isBeneficial()
                ? tickDuration + 20.0 * mastery
                : tickDuration - 20.0 * immunity;
        final var actualDuration = Mth.clamp(scaledDuration, tickDuration * 0.5, tickDuration * 1.5);
        return new MobEffectInstance(mobEffect(), (int) actualDuration, amplifier());
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                EssenceHolder.CODEC.fieldOf("essence").forGetter(PackedEffectProvider::holder),
                Codec.DOUBLE.fieldOf("weight").forGetter(PackedEffectProvider::weight),
                Codec.DOUBLE.fieldOf("focus").forGetter(PackedEffectProvider::focus)
        ).apply(codec, PackedEffectProvider::new));
    }
}
