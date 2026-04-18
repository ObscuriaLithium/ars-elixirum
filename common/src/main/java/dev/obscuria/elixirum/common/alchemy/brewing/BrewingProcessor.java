package dev.obscuria.elixirum.common.alchemy.brewing;

import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
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

        appendEssences(weights, contributors, recipe.foundation());
        appendEssences(weights, contributors, recipe.catalyst());
        appendEssences(weights, contributors, recipe.stabilizer());

        if (weights.isEmpty()) {
            return ElixirContents.EMPTY;
        }

        if (recipe.catalyst().isPresent()) {
            CatalystProperties catalyst = recipe.catalyst().get().properties(alchemy).catalyst();
            applyShifts(weights, catalyst);
        }

        if (recipe.stabilizer().isPresent()) {
            Aspect stabilizerQ = recipe.stabilizer().get().properties(alchemy).aspect();
            applyConcordance(weights, stabilizerQ);
        }

        var temper = resolveTemper(recipe.foundation().orElse(null));
        var effects = new ArrayList<EffectProvider>(weights.size());

        for (var entry : weights.entrySet()) {
            var essence = entry.getKey();
            double weight = entry.getValue();
            if (weight <= 0.0) continue;

            var count = contributors.getOrDefault(essence, 1);
            var effect = resolve(essence, weight, count, temper);

            if (effect != null) {
                effects.add(effect);
            }
        }

        return ElixirContents.sorted(effects);
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

    private Temper resolveTemper(@Nullable AlchemyIngredient ingredient) {
        if (ingredient == null) return Temper.BALANCED;
        return ingredient.properties(alchemy).foundation().temper();
    }

    private void applyShifts(Map<EssenceHolder, Double> weights, CatalystProperties catalyst) {
        var shifts = catalyst.aspectShifts();
        if (shifts.isEmpty()) return;

        for (AspectShift shift : shifts) {
            Aspect q = shift.aspect();

            for (var entry : weights.entrySet()) {
                var essence = entry.getKey();

                if (essence.require().aspect() == q) {
                    entry.setValue(shift.apply(essence, entry.getValue()));
                }
            }
        }
    }

    private void applyConcordance(Map<EssenceHolder, Double> weights, Aspect stabilizer) {
        for (var entry : weights.entrySet()) {
            var essence = entry.getKey();
            ConcordanceTier tier = getConcordance(essence.require().aspect(), stabilizer);
            entry.setValue(tier.apply(entry.getValue()));
        }
    }

    private EffectProvider resolve(EssenceHolder essence, double weight, int contributors, Temper temper) {
        ManifestationTier tier = getManifestationTier(contributors);
        double finalWeight = Math.round(tier.apply(weight) * 100.0) / 100.0;

        if (!tier.isGuaranteed() && finalWeight < 0.1) {
            //TODO
            return null;
        }

        return new EffectProvider.Packed(essence, finalWeight, temper.value);
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
