package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.StabilizerProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneStabilizerPropertyProvider() implements IStabilizerPropertyProvider {

    public static final NoneStabilizerPropertyProvider SHARED = new NoneStabilizerPropertyProvider();
    public static final Codec<NoneStabilizerPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneStabilizerPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public StabilizerProperties resolve(Item item, RandomSource random) {
        return StabilizerProperties.EMPTY;
    }
}
