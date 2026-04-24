package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface ITemperProvider {

    Codec<ITemperProvider> CODEC = ElixirumRegistries
            .TEMPER_PROVIDER_TYPE.byNameCodec()
            .dispatch(ITemperProvider::codec, Function.identity());

    Codec<? extends ITemperProvider> codec();

    Focus resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends ITemperProvider>> context) {
        context.register("none", () -> NoneTemperProvider.CODEC);
        context.register("generated", () -> GeneratedTemperProvider.CODEC);
        context.register("fixed", () -> FixedTemperProvider.CODEC);
    }
}
