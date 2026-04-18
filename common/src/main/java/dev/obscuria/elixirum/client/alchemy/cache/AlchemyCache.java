package dev.obscuria.elixirum.client.alchemy.cache;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.brewing.BrewingProcessor;
import dev.obscuria.elixirum.common.alchemy.profiles.ConfiguredRecipe;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.common.registry.ElixirumItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AlchemyCache {

    private static final Map<UUID, ConfiguredRecipe> configuredRecipes = new HashMap<>();
    private static final Map<UUID, CachedElixir> cachedElixirs = new HashMap<>();

    public static ConfiguredRecipe configuredRecipeOf(AlchemyRecipe recipe) {
        return configuredRecipes.computeIfAbsent(recipe.uuid(), key ->
                ClientAlchemy.INSTANCE.localProfile()
                        .collection().findConfig(recipe.uuid())
                        .orElseGet(recipe::save));
    }

    public static CachedElixir cachedElixirOf(AlchemyRecipe recipe) {
        return cachedElixirs.computeIfAbsent(recipe.uuid(), key -> {
            var configured = configuredRecipeOf(recipe);
            var stack = ElixirumItems.ELIXIR.instantiate();
            var processor = new BrewingProcessor(ClientAlchemy.INSTANCE, recipe);
            ArsElixirumHelper.setElixirContents(stack, processor.brew());
            ArsElixirumHelper.setStyle(stack, configured.getStyle());
            ArsElixirumHelper.setChroma(stack, configured.getChroma());
            return new CachedElixir(stack, configured);
        });
    }

    public static void clear() {
        configuredRecipes.clear();
        cachedElixirs.clear();
    }
}
