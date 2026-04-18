package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneCatalystPropertyProvider() implements ICatalystPropertyProvider {

    public static final NoneCatalystPropertyProvider SHARED = new NoneCatalystPropertyProvider();
    public static final Codec<NoneCatalystPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneCatalystPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public CatalystProperties resolve(Item item, RandomSource random) {
        return CatalystProperties.EMPTY;
    }
}
