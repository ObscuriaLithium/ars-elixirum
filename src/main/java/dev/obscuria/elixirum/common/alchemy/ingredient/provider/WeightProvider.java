package dev.obscuria.elixirum.common.alchemy.ingredient.provider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientQuality;

public final class WeightProvider extends RangeProvider {

    public static final int MIN_WEIGHT = 1;
    public static final int MAX_WEIGHT = 64;
    public static final WeightProvider DEFAULT;
    public static final Codec<WeightProvider> CODEC;

    public WeightProvider(Either<String, Integer> either) {
        super(either, IngredientQuality::selectWeight, MIN_WEIGHT, MAX_WEIGHT);
    }

    static {
        DEFAULT = new WeightProvider(Either.left("*"));
        CODEC = Codec.either(Codec.STRING, Codec.INT).xmap(WeightProvider::new, it -> it.either);
    }
}
