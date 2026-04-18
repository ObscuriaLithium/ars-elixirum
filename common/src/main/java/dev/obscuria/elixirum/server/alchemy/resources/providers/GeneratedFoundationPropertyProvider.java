package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Temper;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.FoundationProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedFoundationPropertyProvider() implements IFoundationPropertyProvider {

    public static final GeneratedFoundationPropertyProvider SHARED = new GeneratedFoundationPropertyProvider();
    public static final Codec<GeneratedFoundationPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedFoundationPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public FoundationProperties resolve(Item item, RandomSource random) {
        var values = Temper.values();
        return new FoundationProperties(values[random.nextInt(values.length)]);
    }
}
