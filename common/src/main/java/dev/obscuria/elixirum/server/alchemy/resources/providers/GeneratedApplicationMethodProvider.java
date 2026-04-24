package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public record GeneratedApplicationMethodProvider() implements IApplicationMethodProvider {

    public static final GeneratedApplicationMethodProvider SHARED = new GeneratedApplicationMethodProvider();
    public static final Codec<GeneratedApplicationMethodProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedApplicationMethodProvider> codec() {
        return CODEC;
    }

    @Override
    public Form resolve(Item item, RandomSource random) {
        return Form.values()[random.nextInt(Form.values().length)];
    }
}
