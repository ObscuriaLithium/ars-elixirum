package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.Temper;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.FoundationProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedFoundationPropertyProvider(
        Temper temper
) implements IFoundationPropertyProvider {

    public static final Codec<FixedFoundationPropertyProvider> CODEC;

    @Override
    public Codec<FixedFoundationPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public FoundationProperties resolve(Item item, RandomSource random) {
        return new FoundationProperties(temper);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Temper.CODEC.fieldOf("temper").forGetter(FixedFoundationPropertyProvider::temper)
        ).apply(codec, FixedFoundationPropertyProvider::new));
    }
}
