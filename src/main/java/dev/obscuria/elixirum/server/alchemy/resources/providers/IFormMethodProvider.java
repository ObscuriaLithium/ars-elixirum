package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IFormMethodProvider {

    Codec<IFormMethodProvider> CODEC = ElixirumRegistries
            .APPLICATION_METHOD_PROVIDER_TYPE.byNameCodec()
            .dispatch(IFormMethodProvider::codec, Function.identity());

    Codec<? extends IFormMethodProvider> codec();

    Form resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IFormMethodProvider>> context) {
        context.register("none", () -> NoneFormMethodProvider.CODEC);
        context.register("generated", () -> GeneratedFormMethodProvider.CODEC);
        context.register("fixed", () -> FixedFormMethodProvider.CODEC);
    }
}
