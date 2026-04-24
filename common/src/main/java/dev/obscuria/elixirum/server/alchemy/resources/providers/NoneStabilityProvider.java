package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneStabilityProvider() implements IStabilityProvider {

    public static final NoneStabilityProvider SHARED = new NoneStabilityProvider();
    public static final Codec<NoneStabilityProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneStabilityProvider> codec() {
        return CODEC;
    }

    @Override
    public Risk resolve(Item item, RandomSource random) {
        return Risk.BALANCED;
    }
}
