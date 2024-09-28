package dev.obscuria.elixirum.common.alchemy.brewing;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
    private final List<Element> ingredients = Lists.newArrayList();

    public static ElixirContents brew(HolderGetter<Essence> getter, ElixirRecipe recipe) {
        return new BrewingProcessor(getter, recipe).brew();
    }

    private BrewingProcessor(HolderGetter<Essence> getter, ElixirRecipe recipe) {
        final var consumed = Sets.<Item>newHashSet();
        for (var item : recipe.ingredients()) {
            ingredients.add(!consumed.contains(item)
                    ? new Ingredient(getter, item, ServerAlchemy.getIngredients().getProperties(item))
                    : new Empty());
            consumed.add(item);
        }
        for (var i = 0; i < ingredients.size(); i++) {
            final var ingredient = ingredients.get(i);
            for (var affix : ingredient.getProperties().getAffixes())
                affix.apply(this, i);
        }
    }

    public Optional<Element> getElement(int index) {
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
                .flatMap(ingredient -> ingredient.getEssences().entrySet().stream())
                .forEach(entry -> essences.compute(entry.getKey(), (key, value) -> {
                    final var addition = entry.getValue().compute();
                    return value == null ? addition : value + addition;
                }));
        essences.forEach((key, value) -> builder.addEffect(PackedEffect.byWeight(key, value, value)));
        builder.computeContentColor();
        return builder.build();
    }

    public interface Element {

        IngredientProperties getProperties();

        Map<Holder<Essence>, EssenceInfo> getEssences();

        Stream<EssenceInfo> listEssences(Predicate<Essence> predicate);
    }

    private static final class Ingredient implements Element {
        private final Item item;
        private final IngredientProperties properties;
        private final Map<Holder<Essence>, EssenceInfo> essences;

        Ingredient(HolderGetter<Essence> getter, Item item, IngredientProperties properties) {
            this.item = item;
            this.properties = properties;
            this.essences = properties.getEssences(getter).object2IntEntrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> new EssenceInfo(entry.getIntValue())));
        }

        @Override
        public IngredientProperties getProperties() {
            return this.properties;
        }

        @Override
        public Map<Holder<Essence>, EssenceInfo> getEssences() {
            return this.essences;
        }

        public Stream<EssenceInfo> listEssences(Predicate<Essence> predicate) {
            return this.essences.entrySet().stream()
                    .filter(entry -> predicate.test(entry.getKey().value()))
                    .map(Map.Entry::getValue);
        }
    }

    private static final class Empty implements Element {

        @Override
        public IngredientProperties getProperties() {
            return IngredientProperties.EMPTY;
        }

        @Override
        public Map<Holder<Essence>, EssenceInfo> getEssences() {
            return Maps.newHashMap();
        }

        @Override
        public Stream<EssenceInfo> listEssences(Predicate<Essence> predicate) {
            return Stream.empty();
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
