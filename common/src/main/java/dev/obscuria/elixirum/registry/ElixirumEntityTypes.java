package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.entity.ThrownElixirProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ElixirumEntityTypes
{
    Deferred<EntityType<?>, EntityType<ThrownElixirProjectile>> THROWN_ELIXIR = register("thrown_elixir",
            () -> EntityType.Builder.<ThrownElixirProjectile>of(ThrownElixirProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    private static <T extends Entity> Deferred<EntityType<?>, EntityType<T>>
    register(final String name,
             Supplier<EntityType.Builder<T>> builder)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.ENTITY_TYPE,
                Elixirum.key(name),
                () -> builder.get().build(name));
    }

    static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(THROWN_ELIXIR.value().getDescriptionId(), "Thrown Elixir");
    }

    static void init() {}
}
