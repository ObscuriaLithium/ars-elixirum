package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedFormMethodProvider(
        Form value
) implements IFormMethodProvider {

    public static final Codec<FixedFormMethodProvider> CODEC;

    @Override
    public Codec<FixedFormMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return value;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Form.CODEC.fieldOf("value").forGetter(FixedFormMethodProvider::value)
        ).apply(codec, FixedFormMethodProvider::new));
    }
}
