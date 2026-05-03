package dev.obscuria.elixirum.common.alchemy.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
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

public record _ElixirContents(
        List<EffectProvider> effects,
        Focus focus,
        Form form,
        Risk risk,
        RGB color
) implements ElixirContents {

    public static final _ElixirContents EMPTY;
    public static final _ElixirContents WATER;
    public static final Codec<ElixirContents> CODEC;

    public static ElixirContents create(List<EffectProvider> effects, Focus focus, Form form, Risk risk) {
        var sortedEffects = effects.stream().sorted().toList();
        return new _ElixirContents(
                sortedEffects, focus, form, risk,
                sortedEffects.isEmpty()
                        ? _ElixirContents.WATER.color()
                        : sortedEffects.get(0).color());
    }

    public static ElixirContents create(List<EffectProvider> effects, Focus focus, Form form, Risk risk, RGB color) {
        return new _ElixirContents(effects.stream().sorted().toList(), focus, form, risk, color);
    }

    @Override
    public double quality() {
        return !effects.isEmpty()
                ? effects.get(0).quality()
                : 0.0;
    }

    @Override
    public Aspect aspect() {
        return !effects.isEmpty()
                ? effects.get(0).holder().aspect()
                : Aspect.NONE;
    }

    @Override
    public Component displayName() {
        return !effects.isEmpty()
                ? effects.get(0).displayName()
                : Component.literal("Water");
    }

    @Override
    public boolean isEmpty() {
        return effects.isEmpty();
    }

    @Override
    public boolean isVoided() {
        return effects.isEmpty() || effects.stream().allMatch(EffectProvider::isVoided);
    }

    @Override
    public boolean hasInstantEffects() {
        for (var effect : effects) {
            if (effect.isVoided()) continue;
            if (!effect.isInstant()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasSideEffects() {
        if (risk == Risk.PERFECT) return false;
        for (var effect : effects) {
            if (!effect.isVoided()) continue;
            return true;
        }
        return false;
    }

    @Override
    public double sideEffectWeight() {
        var weight = 0.0;
        for (var effect : effects) {
            if (!effect.isVoided()) continue;
            weight += effect.quality();
        }
        return weight;
    }

    @Override
    public double sideEffectProbability() {
        var probability = risk.modifySideEffectProbability((10.0 + sideEffectWeight()) * 0.01);
        return Mth.clamp(probability, 0.0, 1.0);
    }

    @Override
    public ElixirContents scale(double factor) {
        return ElixirContents.create(
                effects.stream().map(effect -> effect.scale(factor)).toList(),
                focus, form, risk, color);
    }

    @Override
    public void apply(LivingEntity target, @Nullable Entity direct, @Nullable Entity source) {
        forEachEffect(it -> addEffect(target, direct, source, it));
    }

    @Override
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
                Focus.CODEC.optionalFieldOf("focus", Focus.BALANCED).forGetter(ElixirContents::focus),
                Form.CODEC.optionalFieldOf("form", Form.POTABLE).forGetter(ElixirContents::form),
                Risk.CODEC.optionalFieldOf("risk", Risk.BALANCED).forGetter(ElixirContents::risk),
                RGB.CODEC.fieldOf("color").forGetter(ElixirContents::color)
        ).apply(codec, ElixirContents::create));
        EMPTY = new _ElixirContents(List.of(),
                Focus.BALANCED,
                Form.POTABLE,
                Risk.BALANCED,
                Colors.rgbOf(0xFFFFFF));
        WATER = new _ElixirContents(List.of(),
                Focus.BALANCED,
                Form.POTABLE,
                Risk.BALANCED,
                Colors.rgbOf(0x5575DD));
    }
}
