package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedRiskProvider() implements IRiskProvider {

    public static final GeneratedRiskProvider SHARED = new GeneratedRiskProvider();
    public static final Codec<GeneratedRiskProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedRiskProvider> codec() {
        return CODEC;
    }

    @Override
    public Risk resolve(Item item, RandomSource random) {
        return Risk.values()[random.nextInt(Risk.values().length)];
    }
}
