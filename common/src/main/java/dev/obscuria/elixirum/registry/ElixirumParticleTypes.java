package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.particle.ElixirBubbleParticleOptions;
import dev.obscuria.elixirum.common.particle.ElixirSplashParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public interface ElixirumParticleTypes
{
    Deferred<ParticleType<?>, ParticleType<ElixirSplashParticleOptions>> ELIXIR_SPLASH = register("elixir_splash", ElixirSplashParticleOptions.TYPE);
    Deferred<ParticleType<?>, ParticleType<ElixirBubbleParticleOptions>> ELIXIR_BUBBLE = register("elixir_bubble", ElixirBubbleParticleOptions.TYPE);

    private static <T extends ParticleOptions> Deferred<ParticleType<?>, ParticleType<T>>
    register(final String name, ParticleType<T> value)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.PARTICLE_TYPE,
                Elixirum.key(name),
                () -> value);
    }

    static void init() {}
}
