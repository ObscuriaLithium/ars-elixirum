package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.brewing.AspectShift;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.List;

public record FixedCatalystPropertyProvider(
        List<AspectShift> aspectShifts
) implements ICatalystPropertyProvider {

    public static final Codec<FixedCatalystPropertyProvider> CODEC;

    @Override
    public Codec<FixedCatalystPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public CatalystProperties resolve(Item item, RandomSource random) {
        return new CatalystProperties(aspectShifts);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AspectShift.CODEC.listOf().fieldOf("aspect_shifts").forGetter(FixedCatalystPropertyProvider::aspectShifts)
        ).apply(codec, FixedCatalystPropertyProvider::new));
    }
}
