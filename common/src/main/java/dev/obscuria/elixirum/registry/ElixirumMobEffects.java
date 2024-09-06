package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.effect.GrowMobEffect;
import dev.obscuria.elixirum.common.effect.ShrinkMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public interface ElixirumMobEffects {
    LazyRegister<MobEffect> SOURCE = LazyRegister.create(BuiltInRegistries.MOB_EFFECT, Elixirum.MODID);

    LazyValue<MobEffect, GrowMobEffect> GROW = simple("grow", GrowMobEffect::new);
    LazyValue<MobEffect, ShrinkMobEffect> SHRINK = simple("shrink", ShrinkMobEffect::new);

    private static <TValue extends MobEffect> LazyValue<MobEffect, TValue>
    simple(final String name, Supplier<TValue> supplier) {
        return SOURCE.register(name, supplier);
    }
}
