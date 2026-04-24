package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedStabilityProvider(
        Risk value
) implements IStabilityProvider {

    public static final Codec<FixedStabilityProvider> CODEC;

    @Override
    public Codec<FixedStabilityProvider> codec() {
        return CODEC;
    }

    @Override
    public Risk resolve(Item item, RandomSource random) {
        return value;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Risk.CODEC.fieldOf("value").forGetter(FixedStabilityProvider::value)
        ).apply(codec, FixedStabilityProvider::new));
    }
}
