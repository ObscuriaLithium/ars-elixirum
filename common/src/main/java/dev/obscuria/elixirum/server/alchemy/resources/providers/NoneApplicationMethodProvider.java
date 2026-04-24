package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record NoneApplicationMethodProvider() implements IApplicationMethodProvider {

    public static final NoneApplicationMethodProvider SHARED = new NoneApplicationMethodProvider();
    public static final Codec<NoneApplicationMethodProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<NoneApplicationMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return Form.POTABLE;
    }
}
