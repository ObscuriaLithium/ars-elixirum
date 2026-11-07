package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedFormMethodProvider() implements IFormMethodProvider {

    public static final GeneratedFormMethodProvider SHARED = new GeneratedFormMethodProvider();
    public static final Codec<GeneratedFormMethodProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedFormMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return Form.values()[random.nextInt(Form.values().length)];
    }
}
