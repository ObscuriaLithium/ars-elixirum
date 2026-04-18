package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.FoundationProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneFoundationPropertyProvider() implements IFoundationPropertyProvider {

    public static final NoneFoundationPropertyProvider SHARED = new NoneFoundationPropertyProvider();
    public static final Codec<NoneFoundationPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneFoundationPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public FoundationProperties resolve(Item item, RandomSource random) {
        return FoundationProperties.BALANCED;
    }
}
