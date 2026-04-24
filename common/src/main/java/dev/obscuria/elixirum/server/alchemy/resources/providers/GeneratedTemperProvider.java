package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedTemperProvider() implements ITemperProvider {

    public static final GeneratedTemperProvider SHARED = new GeneratedTemperProvider();
    public static final Codec<GeneratedTemperProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedTemperProvider> codec() {
        return CODEC;
    }

    @Override
    public Focus resolve(Item item, RandomSource random) {
        return Focus.values()[random.nextInt(Focus.values().length)];
    }
}
