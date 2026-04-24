package dev.obscuria.elixirum.api.codex;

import dev.obscuria.archivist.api.v1.components.ComponentHolder;
import dev.obscuria.elixirum.common.alchemy.codex.ProfileComponents;
import dev.obscuria.elixirum.common.alchemy.codex.components.*;

public interface AlchemyProfile extends ComponentHolder {

    default AlchemyMastery mastery() {
        return getOrCreate(ProfileComponents.MASTERY, AlchemyMastery::empty);
    }

    default KnownEffects knownEffects() {
        return getOrCreate(ProfileComponents.KNOWN_EFFECTS, KnownEffects::empty);
    }

    default KnownRecipes knownRecipes() {
        return getOrCreate(ProfileComponents.KNOWN_RECIPES, KnownRecipes::empty);
    }

    default KnownIngredients knownIngredients() {
        return getOrCreate(ProfileComponents.KNOWN_INGREDIENTS, KnownIngredients::empty);
    }

    default RecipeCollection recipeCollection() {
        return getOrCreate(ProfileComponents.RECIPE_COLLECTION, RecipeCollection::empty);
    }
}
