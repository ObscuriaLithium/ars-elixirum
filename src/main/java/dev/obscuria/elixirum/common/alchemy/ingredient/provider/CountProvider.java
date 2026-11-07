package dev.obscuria.elixirum.common.alchemy.ingredient.provider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientQuality;

public final class CountProvider extends RangeProvider {

    public static final int MIN_WEIGHT = 1;
    public static final int MAX_WEIGHT = 8;
    public static final CountProvider DEFAULT;
    public static final Codec<CountProvider> CODEC;

    public CountProvider(Either<String, Integer> either) {
        super(either, IngredientQuality::selectCount, MIN_WEIGHT, MAX_WEIGHT);
    }

    static {
        DEFAULT = new CountProvider(Either.left("*"));
        CODEC = Codec.either(Codec.STRING, Codec.INT).xmap(CountProvider::new, it -> it.either);
    }
}
