package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record FixedApplicationMethodProvider(
        Form value
) implements IApplicationMethodProvider {

    public static final Codec<FixedApplicationMethodProvider> CODEC;

    @Override
    public Codec<FixedApplicationMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return value;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Form.CODEC.fieldOf("value").forGetter(FixedApplicationMethodProvider::value)
        ).apply(codec, FixedApplicationMethodProvider::new));
    }
}
