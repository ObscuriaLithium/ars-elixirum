package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneEssenceProvider() implements IEssenceProvider {

    public static final NoneEssenceProvider SHARED = new NoneEssenceProvider();
    public static final Codec<NoneEssenceProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneEssenceProvider> codec() {
        return CODEC;
    }

    @Override
    public EssenceHolderMap resolve(Item item, RandomSource random) {
        return EssenceHolderMap.EMPTY;
    }

    @Override
    public Aspect resolveAspect(EssenceHolderMap essences) {
        return Aspect.NONE;
    }
}
