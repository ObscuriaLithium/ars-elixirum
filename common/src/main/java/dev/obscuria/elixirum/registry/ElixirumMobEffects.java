package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.effect.GrowMobEffect;
import dev.obscuria.elixirum.common.effect.ShrinkMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public enum ElixirumMobEffects {
    GROW("grow", GrowMobEffect::new),
    SHRINK("shrink", ShrinkMobEffect::new);

    private final Holder<MobEffect> holder;

    ElixirumMobEffects(String name, Supplier<MobEffect> supplier) {
        this.holder = Elixirum.PLATFORM.registerReference(
                BuiltInRegistries.MOB_EFFECT, Elixirum.key(name),
                supplier);
    }

    public Holder<MobEffect> holder() {
        return this.holder;
    }

    public MobEffect value() {
        return this.holder.value();
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(GROW.value().getDescriptionId(), "Grow");
        consumer.accept(SHRINK.value().getDescriptionId(), "Shrink");
    }

    public static void init() {}
}
