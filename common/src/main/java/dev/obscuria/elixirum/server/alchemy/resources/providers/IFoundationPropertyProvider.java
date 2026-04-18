package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.FoundationProperties;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IFoundationPropertyProvider {

    Codec<IFoundationPropertyProvider> CODEC = ElixirumRegistries
            .FOUNDATION_PROPERTY_PROVIDER_TYPE.byNameCodec()
            .dispatch(IFoundationPropertyProvider::codec, Function.identity());

    Codec<? extends IFoundationPropertyProvider> codec();

    FoundationProperties resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IFoundationPropertyProvider>> context) {
        context.register("none", () -> NoneFoundationPropertyProvider.CODEC);
        context.register("generated", () -> GeneratedFoundationPropertyProvider.CODEC);
        context.register("fixed", () -> FixedFoundationPropertyProvider.CODEC);
    }
}
