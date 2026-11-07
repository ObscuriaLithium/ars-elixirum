package dev.obscuria.elixirum.common.alchemy.ingredient.provider;

import com.mojang.datafixers.util.Either;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Function;

abstract class RangeProvider implements IntProvider {

    protected final Either<String, Integer> either;
    private final IntProvider defaultProvider;
    private final int min;
    private final int max;

    protected RangeProvider(Either<String, Integer> either, IntProvider defaultProvider, int min, int max) {
        this.either = either;
        this.defaultProvider = defaultProvider;
        this.min = min;
        this.max = max;
    }

    @Override
    public int compute(Item item, RandomSource random) {
        return either.map(it -> computeInRange(it, item, random), Function.identity());
    }

    private int computeInRange(String input, Item item, RandomSource random) {
        try {
            if (input.equals("*")) return defaultProvider.compute(item, random);
            if (!input.contains("~")) return Mth.clamp(Integer.parseInt(input), min, max);
            final var elements = input.split("~");
            final var actualMin = Math.max(min, Integer.parseInt(elements[0]));
            final var actualMax = Math.min(max, Integer.parseInt(elements[1]));
            return random.nextIntBetweenInclusive(actualMin, actualMax);
        } catch (Exception ignored) {
            return 1;
        }
    }
}
