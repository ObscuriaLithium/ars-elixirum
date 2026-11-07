package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.entity.ThrownElixirProjectile;
import dev.obscuria.fragmentum.registry.DeferredEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public interface ElixirumEntities {

    DeferredEntity<ThrownElixirProjectile> THROWN_ELIXIR = register("thrown_elixir",
            EntityType.Builder.<ThrownElixirProjectile>of(ThrownElixirProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    private static <T extends Entity> DeferredEntity<T> register(String name, EntityType.Builder<T> builder) {
        return ElixirumRegistries.REGISTRAR.registerEntity(ArsElixirum.identifier(name), () -> builder.build(name));
    }

    static void init() {}
}
