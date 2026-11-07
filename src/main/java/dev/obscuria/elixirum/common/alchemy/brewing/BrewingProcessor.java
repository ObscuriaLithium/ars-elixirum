package dev.obscuria.elixirum.common.alchemy.brewing;

import dev.obscuria.elixirum.api.alchemy.AlchemyIngredient;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.providers.PackedEffectProvider;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BrewingProcessor {

    private final Alchemy alchemy;
    private final AlchemyRecipe recipe;

    public BrewingProcessor(Alchemy alchemy, AlchemyRecipe recipe) {
        this.recipe = recipe;
        this.alchemy = alchemy;
    }

    public ElixirContents brew() {

        if (recipe.isEmpty()) {
            return ElixirContents.empty();
        }

        var weights = new HashMap<EssenceHolder, Double>(8);
        var contributors = new HashMap<EssenceHolder, Integer>(8);

        appendEssences(weights, contributors, recipe.base());
        appendEssences(weights, contributors, recipe.catalyst());
        appendEssences(weights, contributors, recipe.inhibitor());

        if (weights.isEmpty()) {
            return ElixirContents.empty();
        }

        var diversity = resolveDiversity();
        var concordance = recipe.inhibitor()
                .map(i -> i.properties(alchemy).aspect())
                .map(this::resolveGlobalConcordance)
                .orElse(Concordance.NONE);

        var focus = resolveFocus(recipe.base().orElse(null));
        var effects = new ArrayList<EffectProvider>(weights.size());

        for (var entry : weights.entrySet()) {
            var essence = entry.getKey();
            double weight = entry.getValue();
            if (weight <= 0.0) continue;

            var count = contributors.getOrDefault(essence, 1);
            var effect = resolve(essence, weight, count, diversity, concordance, focus);
            effects.add(effect);
        }

        return ElixirContents.create(effects, focus,
                recipe.catalyst().map(this::resolveApplicationMethod).orElse(Form.POTABLE),
                recipe.inhibitor().map(this::resolveStability).orElse(Risk.BALANCED));
    }

    private Form resolveApplicationMethod(AlchemyIngredient ingredient) {
        return ingredient.properties(alchemy).form();
    }

    private Risk resolveStability(AlchemyIngredient ingredient) {
        return ingredient.properties(alchemy).risk();
    }

    private void appendEssences(
            Map<EssenceHolder, Double> weights,
            Map<EssenceHolder, Integer> contributors,
            Optional<AlchemyIngredient> ingredientOpt
    ) {
        if (ingredientOpt.isEmpty()) return;

        var properties = ingredientOpt.get().properties(alchemy);
        if (properties.isEmpty()) return;

        for (var entry : properties.essences().sorted()) {
            var essence = entry.getKey();
            var value = (double) entry.getIntValue();

            Double prev = weights.get(essence);
            if (prev == null) {
                weights.put(essence, value);
                contributors.put(essence, 1);
            } else {
                weights.put(essence, prev + value);
                contributors.put(essence, contributors.get(essence) + 1);
            }
        }
    }

    private Focus resolveFocus(@Nullable AlchemyIngredient ingredient) {
        if (ingredient == null) return Focus.BALANCED;
        return ingredient.properties(alchemy).focus();
    }

    private Diversity resolveDiversity() {
        var slots = List.of(recipe.base(), recipe.catalyst(), recipe.inhibitor());

        var present = slots.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        long uniqueCount = present.stream().distinct().count();

        if (uniqueCount == present.size()) return Diversity.DISTINCT;
        if (uniqueCount == 1) return Diversity.UNIFORM;
        return Diversity.REPEATED;
    }

    private Concordance resolveGlobalConcordance(Aspect inhibitorAspect) {
        int matches = 0;

        for (var slot : List.of(recipe.base(), recipe.catalyst(), recipe.inhibitor())) {
            if (slot.isEmpty()) continue;
            var aspect = slot.get().properties(alchemy).aspect();
            if (aspect == inhibitorAspect) matches++;
        }

        return switch (matches) {
            case 3 -> Concordance.PERFECT;
            case 2 -> Concordance.PARTIAL;
            default -> Concordance.NONE;
        };
    }

    private EffectProvider resolve(
            EssenceHolder essence,
            double weight,
            int contributors,
            Diversity diversity,
            Concordance concordance,
            Focus focus
    ) {
        weight = getManifestationTier(contributors).apply(weight);
        weight = diversity.apply(weight);
        weight = concordance.apply(weight);
        weight = Math.min(weight, 100.0);

        var finalWeight = Math.round(weight * 100.0) / 100.0;
        return new PackedEffectProvider(essence, finalWeight, focus.value);
    }

    private Manifestation getManifestationTier(int contributors) {
        return switch (contributors) {
            case 3 -> Manifestation.DOMINANT;
            case 2 -> Manifestation.PRIMARY;
            default -> Manifestation.TRACE;
        };
    }
}