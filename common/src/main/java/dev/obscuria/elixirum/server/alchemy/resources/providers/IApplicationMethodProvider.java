package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IApplicationMethodProvider {

    Codec<IApplicationMethodProvider> CODEC = ElixirumRegistries
            .APPLICATION_METHOD_PROVIDER_TYPE.byNameCodec()
            .dispatch(IApplicationMethodProvider::codec, Function.identity());

    Codec<? extends IApplicationMethodProvider> codec();

    Form resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IApplicationMethodProvider>> context) {
        context.register("none", () -> NoneApplicationMethodProvider.CODEC);
        context.register("generated", () -> GeneratedApplicationMethodProvider.CODEC);
        context.register("fixed", () -> FixedApplicationMethodProvider.CODEC);
    }
}
