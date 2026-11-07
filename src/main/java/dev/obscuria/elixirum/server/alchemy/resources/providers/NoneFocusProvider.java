package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneFocusProvider() implements IFocusProvider {

    public static final NoneFocusProvider SHARED = new NoneFocusProvider();
    public static final Codec<NoneFocusProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneFocusProvider> codec() {
        return CODEC;
    }

    @Override
    public Focus resolve(Item item, RandomSource random) {
        return Focus.BALANCED;
    }
}
