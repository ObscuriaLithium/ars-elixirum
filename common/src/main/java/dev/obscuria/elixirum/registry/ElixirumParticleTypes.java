package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.particle.ElixirBubbleParticleOptions;
import dev.obscuria.elixirum.common.particle.ElixirSplashParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public interface ElixirumParticleTypes {
    ParticleType<ElixirSplashParticleOptions> ELIXIR_SPLASH = register("elixir_splash", ElixirSplashParticleOptions.TYPE);
    ParticleType<ElixirBubbleParticleOptions> ELIXIR_BUBBLE = register("elixir_bubble", ElixirBubbleParticleOptions.TYPE);

    private static <T extends ParticleOptions> ParticleType<T>
    register(final String name, ParticleType<T> value) {
        Elixirum.PLATFORM.registerReference(BuiltInRegistries.PARTICLE_TYPE, Elixirum.key(name), () -> value);
        return value;
    }

    static void setup() {}
}
