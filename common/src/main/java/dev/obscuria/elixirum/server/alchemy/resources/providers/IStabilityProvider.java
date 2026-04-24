package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IStabilityProvider {

    Codec<IStabilityProvider> CODEC = ElixirumRegistries
            .STABILITY_PROVIDER_TYPE.byNameCodec()
            .dispatch(IStabilityProvider::codec, Function.identity());

    Codec<? extends IStabilityProvider> codec();

    Risk resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IStabilityProvider>> context) {
        context.register("none", () -> NoneStabilityProvider.CODEC);
        context.register("generated", () -> GeneratedStabilityProvider.CODEC);
        context.register("fixed", () -> FixedStabilityProvider.CODEC);
    }
}
