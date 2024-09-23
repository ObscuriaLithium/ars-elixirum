package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.particle.ElixirBubbleParticleOptions;
import dev.obscuria.elixirum.common.particle.ElixirSplashParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@SuppressWarnings("all")
@ApiStatus.NonExtendable
public interface ElixirumParticleTypes {
    LazyRegister<ParticleType<?>> SOURCE = LazyRegister.create(BuiltInRegistries.PARTICLE_TYPE, Elixirum.MODID);

    LazyValue<ParticleType<?>, ParticleType<ElixirSplashParticleOptions>> ELIXIR_SPLASH = simple("elixir_splash", () -> ElixirSplashParticleOptions.TYPE);
    LazyValue<ParticleType<?>, ParticleType<ElixirBubbleParticleOptions>> ELIXIR_BUBBLE = simple("elixir_bubble", () -> ElixirBubbleParticleOptions.TYPE);

    private static <TValue extends ParticleOptions> LazyValue<ParticleType<?>, ParticleType<TValue>>
    simple(final String name, Supplier<ParticleType<TValue>> supplier) {
        return SOURCE.register(name, supplier);
    }
}
