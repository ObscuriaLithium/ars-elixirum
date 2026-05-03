package dev.obscuria.elixirum.api.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.providers.DirectEffectProvider;
import dev.obscuria.elixirum.common.alchemy.providers.PackedEffectProvider;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public interface EffectProvider extends Comparable<EffectProvider> {

    Codec<EffectProvider> CODEC = CodecHelper.registryDispatch(
            ElixirumRegistries.EFFECT_PROVIDER_TYPE,
            EffectProvider::codec);

    Codec<? extends EffectProvider> codec();

    EssenceHolder holder();

    double quality();

    int amplifier();

    int duration();

    EffectProvider splitBy(int amount);

    EffectProvider scale(double factor);

    EffectProvider withWeight(double weight);

    MobEffectInstance instantiate(double mastery, double immunity);

    default EffectProvider substract(int weight) {
        return withWeight(quality() - weight);
    }

    default MobEffect mobEffect() {
        if (!holder().isBound()) return MobEffects.UNLUCK;
        return holder().value().effect().value();
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

    default boolean isValid() {
        return quality() > 0;
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

    @Override
    default int compareTo(EffectProvider other) {
        var result = Double.compare(other.quality(), quality());
        if (result != 0) return result;
        return mobEffect().getDescriptionId().compareTo(other.mobEffect().getDescriptionId());
    }

    static void bootstrap(BootstrapContext<Codec<? extends EffectProvider>> context) {
        context.register("packed", () -> PackedEffectProvider.CODEC);
        context.register("direct", () -> DirectEffectProvider.CODEC);
    }
}