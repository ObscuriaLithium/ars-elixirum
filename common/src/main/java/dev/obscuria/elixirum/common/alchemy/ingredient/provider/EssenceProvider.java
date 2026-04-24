package dev.obscuria.elixirum.common.alchemy.ingredient.provider;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientQuality;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record EssenceProvider(Either<String, Map<EssenceHolder, WeightProvider>> either) {

    public static final EssenceProvider DEFAULT;
    public static final Codec<EssenceProvider> CODEC;

    public Map<EssenceHolder, Integer> select(Item item, RandomSource random, int count) {
        return either.map(
                it -> any(item, random, count),
                it -> scoped(it, item, random, count));
    }

    private Map<EssenceHolder, Integer> any(Item item, RandomSource random, int count) {
        final var essences = new ArrayList<>(Alchemy.guess().essences().streamHolders().toList());
        return pickRandom(essences, random, count).stream().collect(Collectors.toMap(Function.identity(),
                it -> IngredientQuality.selectWeight(item, random)));
    }

    private Map<EssenceHolder, Integer> scoped(Map<EssenceHolder, WeightProvider> scope, Item item, RandomSource random, int count) {
        if (scope.isEmpty()) return any(item, random, count);
        final var entries = new ArrayList<>(scope.entrySet());
        return pickRandom(entries, random, count).stream().collect(Collectors.toMap(Map.Entry::getKey,
                it -> it.getValue().compute(item, random)));
    }

    private <T> List<T> pickRandom(List<T> source, RandomSource random, int count) {
        if (source.isEmpty()) return Lists.newArrayList();
        final var copy = Lists.newArrayList(source);
        final var result = Lists.<T>newArrayList();
        while (!copy.isEmpty() && result.size() < count) {
            result.add(copy.remove(random.nextInt(copy.size())));
        }
        return result;
    }

    static {
        DEFAULT = new EssenceProvider(Either.left("*"));
        CODEC = Codec
                .either(Codec.STRING, Codec.unboundedMap(EssenceHolder.CODEC, WeightProvider.CODEC))
                .xmap(EssenceProvider::new, EssenceProvider::either);
    }
}
