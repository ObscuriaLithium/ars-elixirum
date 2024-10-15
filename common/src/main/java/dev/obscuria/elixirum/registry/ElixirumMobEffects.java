package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.effect.GrowMobEffect;
import dev.obscuria.elixirum.common.effect.ShrinkMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ElixirumMobEffects
{
    Deferred<MobEffect, GrowMobEffect> GROW = register("grow",GrowMobEffect::new);
    Deferred<MobEffect, ShrinkMobEffect> SHRINK = register("shrink", ShrinkMobEffect::new);

    private static <T extends MobEffect> Deferred<MobEffect, T>
    register(final String name,
             Supplier<T> supplier)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.MOB_EFFECT,
                Elixirum.key(name),
                supplier);
    }

    static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(GROW.value().getDescriptionId(), "Grow");
        consumer.accept(SHRINK.value().getDescriptionId(), "Shrink");
    }

    static void init() {}
}
