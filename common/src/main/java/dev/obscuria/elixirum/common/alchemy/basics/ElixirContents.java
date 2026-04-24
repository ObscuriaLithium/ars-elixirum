package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public record ElixirContents(
        List<EffectProvider> effects,
        Form form,
        Risk risk,
        Focus focus,
        RGB color
) {

    public static final ElixirContents EMPTY;
    public static final ElixirContents WATER;
    public static final Codec<ElixirContents> CODEC;

    public static ElixirContents create(
            List<EffectProvider> effects,
            Form method,
            Risk risk,
            Focus focus) {
        var sortedEffects = effects.stream().sorted().toList();
        return new ElixirContents(
                sortedEffects, method, risk, focus,
                sortedEffects.isEmpty()
                        ? ElixirContents.WATER.color()
                        : sortedEffects.get(0).color());
    }

    public static ElixirContents create(
            List<EffectProvider> effects,
            Form method,
            Risk risk,
            Focus focus,
            RGB color) {
        return new ElixirContents(
                effects.stream().sorted().toList(),
                method, risk, focus, color);
    }

    public boolean isEmpty() {
        return effects.isEmpty();
    }

    public boolean hasInstantEffects() {
        for (var effect : effects) {
            if (effect.isVoided()) continue;
            if (!effect.isInstant()) continue;
            return true;
        }
        return false;
    }

    public boolean hasSideEffects() {
        if (risk == Risk.PERFECT) return false;
        for (var effect : effects) {
            if (!effect.isVoided()) continue;
            return true;
        }
        return false;
    }

    public double sideEffectWeight() {
        var weight = 0.0;
        for (var effect : effects) {
            if (!effect.isVoided()) continue;
            weight += effect.quality();
        }
        return weight;
    }

    public double sideEffectProbability() {
        var probability = risk.modifySideEffectProbability((10.0 + sideEffectWeight()) * 0.01);
        return Mth.clamp(probability, 0.0, 1.0);
    }

    public Component displayName() {
        return effects.isEmpty()
                ? Component.literal("Water")
                : effects.get(0).displayName();
    }

    public Aspect aspect() {
        return effects.isEmpty() ? Aspect.NONE : effects.get(0).holder().map(Essence::aspect).orElse(Aspect.NONE);
    }

    public double quality() {
        return effects.isEmpty() ? 0.0 : effects.get(0).quality();
    }

    public boolean isVoided() {
        return effects.isEmpty() || effects.stream().allMatch(EffectProvider::isVoided);
    }

    public ElixirContents scale(double factor) {
        return new ElixirContents(
                effects.stream().map(effect -> effect.scale(factor)).toList(),
                form, risk, focus, color);
    }

    public void apply(
            LivingEntity target,
            @Nullable Entity direct,
            @Nullable Entity source
    ) {
        forEachEffect(it -> addEffect(target, direct, source, it));
    }

    public void forEachEffect(Consumer<MobEffectInstance> consumer) {
        for (var provider : effects) {
            if (provider.isVoided()) continue;
            var instance = provider.instantiate(0, 0);
            consumer.accept(instance);
        }

        var probability = sideEffectProbability();
        if (probability <= 0.0) return;
        if (ArsElixirum.SHARED_RANDOM.nextDouble() > probability) return;

        var sideEffects = effects.stream().filter(EffectProvider::isVoided).toList();
        if (sideEffects.isEmpty()) return;
        var effect = sideEffects.get(ArsElixirum.SHARED_RANDOM.nextInt(sideEffects.size()));
        consumer.accept(effect.withWeight(sideEffectWeight()).instantiate(0, 0));
    }

    private void addEffect(
            LivingEntity target,
            @Nullable Entity direct,
            @Nullable Entity source,
            MobEffectInstance instance
    ) {
        if (instance.getEffect().isInstantenous()) {
            instance.getEffect().applyInstantenousEffect(direct, source, target, instance.getAmplifier(), 1);
        } else {
            target.addEffect(instance);
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                EffectProvider.CODEC.listOf().fieldOf("effects").forGetter(ElixirContents::effects),
                Form.CODEC.optionalFieldOf("form", Form.POTABLE).forGetter(ElixirContents::form),
                Risk.CODEC.optionalFieldOf("risk", Risk.BALANCED).forGetter(ElixirContents::risk),
                Focus.CODEC.optionalFieldOf("focus", Focus.BALANCED).forGetter(ElixirContents::focus),
                RGB.CODEC.fieldOf("color").forGetter(ElixirContents::color)
        ).apply(codec, ElixirContents::create));
        EMPTY = new ElixirContents(List.of(),
                Form.POTABLE,
                Risk.BALANCED,
                Focus.BALANCED,
                Colors.rgbOf(0xFFFFFF));
        WATER = new ElixirContents(List.of(),
                Form.POTABLE,
                Risk.BALANCED,
                Focus.BALANCED,
                Colors.rgbOf(0x5575DD));
    }
}
