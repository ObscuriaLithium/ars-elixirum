package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IFocusProvider {

    Codec<IFocusProvider> CODEC = ElixirumRegistries
            .TEMPER_PROVIDER_TYPE.byNameCodec()
            .dispatch(IFocusProvider::codec, Function.identity());

    Codec<? extends IFocusProvider> codec();

    Focus resolve(Item item, RandomSource random);

    static void bootstrap(BootstrapContext<Codec<? extends IFocusProvider>> context) {
        context.register("none", () -> NoneFocusProvider.CODEC);
        context.register("generated", () -> GeneratedFocusProvider.CODEC);
        context.register("fixed", () -> FixedFocusProvider.CODEC);
    }
}
