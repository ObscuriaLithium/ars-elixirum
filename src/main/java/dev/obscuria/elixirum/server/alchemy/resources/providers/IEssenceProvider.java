package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface IEssenceProvider {

    Codec<IEssenceProvider> CODEC = ElixirumRegistries
            .ESSENCE_PROVIDER_TYPE.byNameCodec()
            .dispatch(IEssenceProvider::codec, Function.identity());

    Codec<? extends IEssenceProvider> codec();

    EssenceHolderMap resolve(Item item, RandomSource random);

    Aspect resolveAspect(EssenceHolderMap essences);

    static void bootstrap(BootstrapContext<Codec<? extends IEssenceProvider>> context) {
        context.register("none", () -> NoneEssenceProvider.CODEC);
        context.register("generated", () -> GeneratedEssenceProvider.CODEC);
        context.register("fixed", () -> FixedEssenceProvider.CODEC);
    }
}
