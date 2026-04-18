package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolderMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedEssenceProvider(
        Aspect aspect,
        EssenceHolderMap essences
) implements IEssenceProvider {

    public static final Codec<FixedEssenceProvider> CODEC;

    @Override
    public Codec<FixedEssenceProvider> codec() {
        return CODEC;
    }

    @Override
    public EssenceHolderMap resolve(Item item, RandomSource random) {
        return essences;
    }

    @Override
    public Aspect resolveAspect(EssenceHolderMap essences) {
        return aspect;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.fieldOf("aspect").forGetter(FixedEssenceProvider::aspect),
                EssenceHolderMap.CODEC.fieldOf("essences").forGetter(FixedEssenceProvider::essences)
        ).apply(codec, FixedEssenceProvider::new));
    }
}
