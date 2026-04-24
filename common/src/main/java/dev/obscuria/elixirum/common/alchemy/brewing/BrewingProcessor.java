package dev.obscuria.elixirum.common.alchemy.brewing;

import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
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
            return ElixirContents.EMPTY;
        }

        var weights = new HashMap<EssenceHolder, Double>(8);
        var contributors = new HashMap<EssenceHolder, Integer>(8);

        appendEssences(weights, contributors, recipe.getBase());
        appendEssences(weights, contributors, recipe.getCatalyst());
        appendEssences(weights, contributors, recipe.getInhibitor());

        if (weights.isEmpty()) {
            return ElixirContents.EMPTY;
        }

        if (recipe.getInhibitor().isPresent()) {
            Aspect stabilizerQ = recipe.getInhibitor().get().properties(alchemy).aspect();
            applyConcordance(weights, stabilizerQ);
        }

        var temper = resolveTemper(recipe.getBase().orElse(null));
        var effects = new ArrayList<EffectProvider>(weights.size());

        for (var entry : weights.entrySet()) {
            var essence = entry.getKey();
            double weight = entry.getValue();
            if (weight <= 0.0) continue;

            var count = contributors.getOrDefault(essence, 1);
            var effect = resolve(essence, weight, count, temper);
            effects.add(effect);
        }

        return ElixirContents.create(effects,
                recipe.getCatalyst().map(this::resolveApplicationMethod).orElse(Form.POTABLE),
                recipe.getInhibitor().map(this::resolveStability).orElse(Risk.BALANCED),
                temper);
    }

    private Form resolveApplicationMethod(AlchemyIngredient ingredient) {
        return ingredient.properties(alchemy).application();
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

    private Focus resolveTemper(@Nullable AlchemyIngredient ingredient) {
        if (ingredient == null) return Focus.BALANCED;
        return ingredient.properties(alchemy).focus();
    }

    private void applyConcordance(Map<EssenceHolder, Double> weights, Aspect stabilizer) {
        for (var entry : weights.entrySet()) {
            var essence = entry.getKey();
            ConcordanceTier tier = getConcordance(essence.aspect(), stabilizer);
            entry.setValue(tier.apply(entry.getValue()));
        }
    }

    private EffectProvider resolve(EssenceHolder essence, double weight, int contributors, Focus focus) {
        var tier = getManifestationTier(contributors);
        var finalWeight = Math.round(tier.apply(weight) * 100.0) / 100.0;
        return new EffectProvider.Packed(essence, finalWeight, focus.value);
    }

    private ManifestationTier getManifestationTier(int contributors) {
        return switch (contributors) {
            case 3 -> ManifestationTier.DOMINANT;
            case 2 -> ManifestationTier.PRIMARY;
            default -> ManifestationTier.TRACE;
        };
    }

    private ConcordanceTier getConcordance(Aspect a, Aspect b) {
        if (a == b) return ConcordanceTier.PERFECT;
        return ConcordanceTier.PARTIAL;
    }
}
