package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.StabilizerProperties;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IStabilizerPropertyProvider {

    Codec<IStabilizerPropertyProvider> CODEC = ElixirumRegistries
            .STABILIZER_PROPERTY_PROVIDER_TYPE.byNameCodec()
            .dispatch(IStabilizerPropertyProvider::codec, Function.identity());

    Codec<? extends IStabilizerPropertyProvider> codec();

    StabilizerProperties resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IStabilizerPropertyProvider>> context) {
        context.register("none", () -> NoneStabilizerPropertyProvider.CODEC);
        context.register("generated", () -> GeneratedStabilizerPropertyProvider.CODEC);
        context.register("fixed", () -> FixedStabilizerPropertyProvider.CODEC);
    }
}
