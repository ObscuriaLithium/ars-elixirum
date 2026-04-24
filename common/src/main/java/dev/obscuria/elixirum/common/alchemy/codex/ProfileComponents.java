package dev.obscuria.elixirum.common.alchemy.codex;

import com.mojang.serialization.Codec;
import dev.obscuria.archivist.api.v1.components.ComponentKey;
import dev.obscuria.elixirum.common.alchemy.codex.components.*;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.registry.BootstrapContext;

public final class ProfileComponents {

    public static final ComponentKey<AlchemyMastery> MASTERY = ComponentKey.create(AlchemyMastery.CODEC);
    public static final ComponentKey<KnownEffects> KNOWN_EFFECTS = ComponentKey.create(KnownEffects.CODEC);
    public static final ComponentKey<KnownRecipes> KNOWN_RECIPES = ComponentKey.create(KnownRecipes.CODEC);
    public static final ComponentKey<KnownIngredients> KNOWN_INGREDIENTS = ComponentKey.create(KnownIngredients.CODEC);
    public static final ComponentKey<RecipeCollection> RECIPE_COLLECTION = ComponentKey.create(RecipeCollection.CODEC);

    public static final Codec<ComponentKey<?>> CODEC;

    public static void bootstrap(BootstrapContext<ComponentKey<?>> context) {
        context.register("mastery", () -> MASTERY);
        context.register("known_effects", () -> KNOWN_EFFECTS);
        context.register("known_recipes", () -> KNOWN_RECIPES);
        context.register("known_ingredients", () -> KNOWN_INGREDIENTS);
        context.register("recipe_collection", () -> RECIPE_COLLECTION);
    }

    static {
        CODEC = ElixirumRegistries.PROFILE_COMPONENT_TYPE.byNameCodec();
    }
}