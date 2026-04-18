package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface ICatalystPropertyProvider {

    Codec<ICatalystPropertyProvider> CODEC = ElixirumRegistries
            .CATALYST_PROPERTY_PROVIDER_TYPE.byNameCodec()
            .dispatch(ICatalystPropertyProvider::codec, Function.identity());

    Codec<? extends ICatalystPropertyProvider> codec();

    CatalystProperties resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends ICatalystPropertyProvider>> context) {
        context.register("none", () -> NoneCatalystPropertyProvider.CODEC);
        context.register("generated", () -> GeneratedCatalystPropertyProvider.CODEC);
        context.register("fixed", () -> FixedCatalystPropertyProvider.CODEC);
    }
}
