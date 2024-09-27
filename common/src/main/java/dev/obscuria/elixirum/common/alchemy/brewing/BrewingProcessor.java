package dev.obscuria.elixirum.common.alchemy.brewing;

import com.google.common.collect.Maps;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.PackedEffect;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientProperties;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BrewingProcessor {
    private final List<BrewingProcessor.Ingredient> ingredients = Lists.newArrayList();

    public static ElixirContents brew(HolderGetter<Essence> getter, ElixirRecipe recipe) {
        return new BrewingProcessor(getter, recipe).brew();
    }

    private BrewingProcessor(HolderGetter<Essence> getter, ElixirRecipe recipe) {
        for (var item : recipe.ingredients())
            ingredients.add(new Ingredient(getter, item,
                    ServerAlchemy.getIngredients().getProperties(item)));
        for (var i = 0; i < ingredients.size(); i++) {
            final var ingredient = ingredients.get(i);
            for (var affix : ingredient.properties.getAffixes())
                affix.apply(this, i);
        }
    }

    public Optional<Ingredient> getIngredient(int index) {
        return index > 0 && index < ingredients.size()
                ? Optional.of(ingredients.get(index))
                : Optional.empty();
    }

    public Stream<EssenceInfo> listEssences(Predicate<Essence> predicate) {
        return ingredients.stream().flatMap(ingredient -> ingredient.listEssences(predicate));
    }

    private ElixirContents brew() {
        final var builder = ElixirContents.create();
        final var essences = Maps.<Holder<Essence>, Double>newHashMap();
        ingredients.stream()
                .flatMap(ingredient -> ingredient.essences.entrySet().stream())
                .forEach(entry -> essences.compute(entry.getKey(), (key, value) -> {
                    final var addition = entry.getValue().compute();
                    return value == null ? addition : value + addition;
                }));
        essences.forEach((key, value) -> builder.addEffect(PackedEffect.byWeight(key, value, value)));
        builder.computeContentColor();
        return builder.build();
    }

    public static final class Ingredient {
        private final Item item;
        private final IngredientProperties properties;
        private final Map<Holder<Essence>, EssenceInfo> essences;

        Ingredient(HolderGetter<Essence> getter, Item item, IngredientProperties properties) {
            this.item = item;
            this.properties = properties;
            this.essences = properties.getEssences(getter).object2IntEntrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> new EssenceInfo(entry.getIntValue())));
        }

        public Stream<EssenceInfo> listEssences(Predicate<Essence> predicate) {
            return essences.entrySet().stream()
                    .filter(entry -> predicate.test(entry.getKey().value()))
                    .map(Map.Entry::getValue);
        }
    }

    public static final class EssenceInfo {
        private final int weight;
        private double modifier;

        EssenceInfo(int weight) {
            this.weight = weight;
            this.modifier = 1.0;
        }

        public void addModifier(double modifier) {
            this.modifier += modifier;
        }

        private double compute() {
            return Math.max(0, weight * modifier);
        }
    }
}
