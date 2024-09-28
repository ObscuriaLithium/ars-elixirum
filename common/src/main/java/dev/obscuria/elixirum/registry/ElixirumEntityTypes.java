package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.entity.ThrownElixirProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;

public interface ElixirumEntityTypes {
    EntityType<ThrownElixirProjectile> THROWN_ELIXIR = register("thrown_elixir",
            EntityType.Builder.<ThrownElixirProjectile>of(ThrownElixirProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    private static <T extends Entity> EntityType<T>
    register(final String name, EntityType.Builder<T> builder) {
        final var value = builder.build(name);
        Elixirum.PLATFORM.registerReference(BuiltInRegistries.ENTITY_TYPE, Elixirum.key(name), () -> value);
        return value;
    }

    static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(THROWN_ELIXIR.getDescriptionId(), "Thrown Elixir");
    }

    static void setup() {}
}
