package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.particle.BubbleParticleOptions;
import dev.obscuria.elixirum.common.world.particle.SplashParticleOptions;
import dev.obscuria.fragmentum.FragmentumFactory;
import dev.obscuria.fragmentum.registry.DeferredParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public interface ElixirumParticles {

    DeferredParticle<BubbleParticleOptions> BUBBLE = register("bubble", () -> FragmentumFactory.newParticleType(false, BubbleParticleOptions.CODEC, BubbleParticleOptions.DESERIALIZER));
    DeferredParticle<SplashParticleOptions> SPLASH = register("splash", () -> FragmentumFactory.newParticleType(false, SplashParticleOptions.CODEC, SplashParticleOptions.DESERIALIZER));

    private static <T extends ParticleOptions> DeferredParticle<T> register(String name, Supplier<ParticleType<T>> supplier) {
        return ElixirumRegistries.REGISTRAR.registerParticle(ArsElixirum.identifier(name), supplier);
    }

    static void init() {}
}
