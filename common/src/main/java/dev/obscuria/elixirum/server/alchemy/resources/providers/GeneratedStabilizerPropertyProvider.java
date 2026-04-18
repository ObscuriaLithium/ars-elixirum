package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.StabilizerProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedStabilizerPropertyProvider() implements IStabilizerPropertyProvider {

    public static final GeneratedStabilizerPropertyProvider SHARED = new GeneratedStabilizerPropertyProvider();
    public static final Codec<GeneratedStabilizerPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedStabilizerPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public StabilizerProperties resolve(Item item, RandomSource random) {
        return StabilizerProperties.EMPTY;
    }
}
