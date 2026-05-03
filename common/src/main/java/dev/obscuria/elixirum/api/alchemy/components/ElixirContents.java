package dev.obscuria.elixirum.api.alchemy.components;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.components._ElixirContents;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ElixirContents {

    static Codec<ElixirContents> codec() {
        return _ElixirContents.CODEC;
    }

    static ElixirContents empty() {
        return _ElixirContents.EMPTY;
    }

    static ElixirContents water() {
        return _ElixirContents.WATER;
    }

    static ElixirContents create(List<EffectProvider> effects, Focus focus, Form form, Risk risk) {
        return _ElixirContents.create(effects, focus, form, risk);
    }

    static ElixirContents create(List<EffectProvider> effects, Focus focus, Form form, Risk risk, RGB color) {
        return _ElixirContents.create(effects, focus, form, risk, color);
    }

    List<EffectProvider> effects();

    Focus focus();

    Form form();

    Risk risk();

    RGB color();

    double quality();

    Aspect aspect();

    Component displayName();

    boolean isEmpty();

    boolean isVoided();

    boolean hasInstantEffects();

    boolean hasSideEffects();

    double sideEffectWeight();

    double sideEffectProbability();

    ElixirContents scale(double factor);

    void apply(LivingEntity target, @Nullable Entity directApplier, @Nullable Entity sourceApplier);

    void forEachEffect(Consumer<MobEffectInstance> consumer);

    default ElixirContents subtract(EssenceHolderMap essences) {
        if (essences.isEmpty() || isEmpty()) return this;
        var result = new ArrayList<EffectProvider>(effects().size());

        for (var effect : effects()) {
            var holder = effect.holder();
            int subtractWeight = essences.get(holder);
            var newEffect = subtractWeight > 0 ? effect.substract(subtractWeight) : effect;
            if (!newEffect.isValid()) continue;
            result.add(newEffect);
        }

        return ElixirContents.create(result, focus(), form(), risk(), color());
    }
}
