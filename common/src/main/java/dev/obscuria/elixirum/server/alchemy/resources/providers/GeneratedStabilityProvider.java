package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedStabilityProvider() implements IStabilityProvider {

    public static final GeneratedStabilityProvider SHARED = new GeneratedStabilityProvider();
    public static final Codec<GeneratedStabilityProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedStabilityProvider> codec() {
        return CODEC;
    }

    @Override
    public Risk resolve(Item item, RandomSource random) {
        return Risk.values()[random.nextInt(Risk.values().length)];
    }
}
