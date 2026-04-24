package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneTemperProvider() implements ITemperProvider {

    public static final NoneTemperProvider SHARED = new NoneTemperProvider();
    public static final Codec<NoneTemperProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneTemperProvider> codec() {
        return CODEC;
    }

    @Override
    public Focus resolve(Item item, RandomSource random) {
        return Focus.BALANCED;
    }
}
