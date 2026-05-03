package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneRiskProvider() implements IRiskProvider {

    public static final NoneRiskProvider SHARED = new NoneRiskProvider();
    public static final Codec<NoneRiskProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneRiskProvider> codec() {
        return CODEC;
    }

    @Override
    public Risk resolve(Item item, RandomSource random) {
        return Risk.BALANCED;
    }
}
