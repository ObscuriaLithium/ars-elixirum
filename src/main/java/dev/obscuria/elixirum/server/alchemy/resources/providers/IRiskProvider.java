package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IRiskProvider {

    Codec<IRiskProvider> CODEC = ElixirumRegistries
            .STABILITY_PROVIDER_TYPE.byNameCodec()
            .dispatch(IRiskProvider::codec, Function.identity());

    Codec<? extends IRiskProvider> codec();

    Risk resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IRiskProvider>> context) {
        context.register("none", () -> NoneRiskProvider.CODEC);
        context.register("generated", () -> GeneratedRiskProvider.CODEC);
        context.register("fixed", () -> FixedRiskProvider.CODEC);
    }
}
