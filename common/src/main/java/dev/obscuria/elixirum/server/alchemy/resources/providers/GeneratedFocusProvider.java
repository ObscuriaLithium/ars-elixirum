package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedFocusProvider() implements IFocusProvider {

    public static final GeneratedFocusProvider SHARED = new GeneratedFocusProvider();
    public static final Codec<GeneratedFocusProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedFocusProvider> codec() {
        return CODEC;
    }

    @Override
    public Focus resolve(Item item, RandomSource random) {
        return Focus.values()[random.nextInt(Focus.values().length)];
    }
}
