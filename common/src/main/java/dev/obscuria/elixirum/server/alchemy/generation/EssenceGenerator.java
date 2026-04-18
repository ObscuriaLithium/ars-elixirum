package dev.obscuria.elixirum.server.alchemy.generation;

import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EssenceGenerator {

    private static final int DEFAULT_MIN_QUALITY = 10;
    private final Map<MobEffect, Limits> effectToLimits = new HashMap<>();

    public EssenceGenerator() {
        BuiltInRegistries.POTION.stream().forEach(potion -> potion
                .getEffects().forEach(instance -> effectToLimits
                        .computeIfAbsent(instance.getEffect(), Limits::new)
                        .recordInstance(instance)));
    }

    public boolean isPotionEffect(MobEffect effect) {
        return effectToLimits.containsKey(effect);
    }

    public Optional<Essence> generateEssence(Holder.Reference<MobEffect> effect) {
        if (!effectToLimits.containsKey(effect.value())) return Optional.empty();
        var limits = effectToLimits.get(effect.value());
        return Optional.of(new Essence(effect,
                limits.maxAmplifier * 2,
                limits.maxDuration * 2,
                DEFAULT_MIN_QUALITY));
    }

    private static class Limits {

        private final MobEffect effect;
        private int maxAmplifier;
        private int maxDuration;

        public Limits(MobEffect effect) {
            this.effect = effect;
        }

        public void recordInstance(MobEffectInstance instance) {
            this.maxAmplifier = Math.max(this.maxAmplifier, instance.getAmplifier());
            this.maxDuration = Math.max(this.maxDuration, instance.getDuration());
        }
    }
}
