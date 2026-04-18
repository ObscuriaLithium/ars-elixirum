package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.brewing.AspectShift;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public record GeneratedCatalystPropertyProvider() implements ICatalystPropertyProvider {

    public static final GeneratedCatalystPropertyProvider SHARED = new GeneratedCatalystPropertyProvider();
    public static final Codec<GeneratedCatalystPropertyProvider> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<GeneratedCatalystPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public CatalystProperties resolve(Item item, RandomSource random) {

        var count = random.nextInt(3) + 1;
        var pool = new ArrayList<>(List.of(Aspect.values()));
        pool.sort((a, b) -> random.nextInt(3) - 1);
        var shifts = new ArrayList<AspectShift>(count);

        for (int i = 0; i < count; i++) {
            var aspect = pool.get(i);
            var sign = random.nextBoolean() ? 1.0 : -1.0;
            var step = random.nextInt(4);
            var magnitude = 0.10 + step * 0.05;
            shifts.add(new AspectShift(aspect, sign * magnitude));
        }

        return new CatalystProperties(shifts);
    }
}
