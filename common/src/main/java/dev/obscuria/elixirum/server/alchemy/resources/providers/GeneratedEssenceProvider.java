package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.ingredient.provider.CountProvider;
import dev.obscuria.elixirum.common.alchemy.ingredient.provider.EssenceProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedEssenceProvider() implements IEssenceProvider {

    public static final GeneratedEssenceProvider SHARED = new GeneratedEssenceProvider();
    public static final Codec<GeneratedEssenceProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedEssenceProvider> codec() {
        return CODEC;
    }

    @Override
    public EssenceHolderMap resolve(Item item, RandomSource random) {
        var essenceCount = CountProvider.DEFAULT.compute(item, random);
        return new EssenceHolderMap(EssenceProvider.DEFAULT.select(item, random, essenceCount));
    }

    public Aspect resolveAspect(EssenceHolderMap essences) {
        return essences.isEmpty()
                ? Aspect.NONE
                : essences.dominant().require().aspect();
    }
}
