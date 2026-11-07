package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedFocusProvider(
        Focus value
) implements IFocusProvider {

    public static final Codec<FixedFocusProvider> CODEC;

    @Override
    public Codec<FixedFocusProvider> codec() {
        return CODEC;
    }

    @Override
    public Focus resolve(Item item, RandomSource random) {
        return value;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Focus.CODEC.fieldOf("value").forGetter(FixedFocusProvider::value)
        ).apply(codec, FixedFocusProvider::new));
    }
}
