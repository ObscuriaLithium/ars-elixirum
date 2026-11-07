package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneFormMethodProvider() implements IFormMethodProvider {

    public static final NoneFormMethodProvider SHARED = new NoneFormMethodProvider();
    public static final Codec<NoneFormMethodProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneFormMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return Form.POTABLE;
    }
}
