package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.helpers.EffectHelper;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.function.Function;

public interface EffectProvider extends Comparable<EffectProvider> {

    Codec<EffectProvider> CODEC = ElixirumRegistries.EFFECT_PROVIDER_TYPE.byNameCodec().dispatch(EffectProvider::codec, Function.identity());

    Codec<? extends EffectProvider> codec();

    EssenceHolder holder();

    double quality();

    int amplifier();

    int duration();

    EffectProvider splitBy(int amount);

    EffectProvider scale(double factor);

    EffectProvider withWeight(double weight);

    MobEffectInstance instantiate(double mastery, double immunity);

    default MobEffect mobEffect() {
        return holder().map(Essence::effect).map(Holder::value).orElse(MobEffects.UNLUCK);
    }

    default boolean isInstant() {
        return mobEffect().isInstantenous();
    }

    default boolean isBeneficial() {
        return mobEffect().isBeneficial();
    }

    default boolean isVoided() {
        return quality() < holder().map(Essence::minQuality).orElse(999);
    }

    default Component displayName() {
        return mobEffect().getDisplayName();
    }

    default Component displayNameWithPotency() {
        if (amplifier() <= 0) return displayName();
        var potency = Component.translatable("potion.potency." + amplifier());
        return Component.translatable("potion.withAmplifier", displayName(), potency);
    }

    default Component statusOrDuration() {
        return mobEffect().isInstantenous()
                ? Component.literal("Instant")
                : Component.literal(StringUtil.formatTickDuration(duration()));
    }

    default RGB color() {
        return Colors.rgbOf(mobEffect().getColor());
    }

    default int compareTo(EffectProvider other) {
        var result = Double.compare(other.quality(), quality());
        if (result != 0) return result;
        return mobEffect().getDescriptionId().compareTo(other.mobEffect().getDescriptionId());
    }

    static void bootstrap(BootstrapContext<Codec<? extends EffectProvider>> context) {
        context.register("packed", () -> Packed.CODEC);
        context.register("direct", () -> Direct.CODEC);
    }

    record Packed(
            EssenceHolder holder,
            double weight,
            double focus
    ) implements EffectProvider {

        public static final Codec<Packed> CODEC;

        @Override
        public Codec<Packed> codec() {
            return CODEC;
        }

        @Override
        public double quality() {
            return weight;
        }

        @Override
        public int amplifier() {
            return holder.map(it -> it.unpackAmplifier(weight * EffectHelper.amplifierFactor(focus))).orElse(0);
        }

        @Override
        public int duration() {
            return holder.map(it -> it.unpackDuration(weight * EffectHelper.durationFactor(focus))).orElse(0);
        }

        @Override
        public EffectProvider splitBy(int amount) {
            return new Packed(holder, weight * (1.0 / amount), focus);
        }

        @Override
        public EffectProvider scale(double factor) {
            return new Packed(holder, weight * factor, focus);
        }

        @Override
        public EffectProvider withWeight(double weight) {
            return new Packed(holder, weight, focus);
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
                    EssenceHolder.CODEC.fieldOf("essence").forGetter(Packed::holder),
                    Codec.DOUBLE.fieldOf("weight").forGetter(Packed::weight),
                    Codec.DOUBLE.fieldOf("focus").forGetter(Packed::focus)
            ).apply(codec, Packed::new));
        }
    }

    record Direct(
            EssenceHolder holder,
            int amplifier,
            int duration
    ) implements EffectProvider {

        public static final Codec<Direct> CODEC;

        @Override
        public Codec<Direct> codec() {
            return CODEC;
        }

        @Override
        public double quality() {
            final var packedAmplifier = holder.map(it -> it.packAmplifier(amplifier)).orElse(0.0);
            final var packedDuration = holder.map(it -> it.packDuration(duration)).orElse(0.0);
            return Mth.clamp((packedAmplifier + packedDuration) * 0.5, 0.0, 100.0);
        }

        @Override
        public EffectProvider splitBy(int amount) {
            final var newAmplifier = amplifier * (1.0 / amount);
            final var newDuration = duration * (1.0 / amount);
            return new Direct(holder, (int) newAmplifier, (int) newDuration);
        }

        @Override
        public EffectProvider scale(double factor) {
            return new Direct(holder, amplifier, (int) Math.ceil(duration * factor));
        }

        @Override
        public EffectProvider withWeight(double weight) {
            return new Direct(holder, amplifier, holder.map(it -> it.unpackDuration(weight)).orElse(0));
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
                    EssenceHolder.CODEC.fieldOf("essence").forGetter(Direct::holder),
                    Codec.INT.fieldOf("amplifier").forGetter(Direct::amplifier),
                    Codec.INT.fieldOf("duration").forGetter(Direct::duration)
            ).apply(codec, Direct::new));
        }
    }
}
