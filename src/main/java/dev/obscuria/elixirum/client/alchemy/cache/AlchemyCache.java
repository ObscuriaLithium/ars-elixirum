package dev.obscuria.elixirum.client.alchemy.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.brewing.BrewingProcessor;
import dev.obscuria.elixirum.common.alchemy.recipes.ConfiguredRecipe;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.helpers.StyleHelper;

import java.time.Duration;

public final class AlchemyCache {

    private static final LoadingCache<AlchemyRecipe, ConfiguredRecipe> CONFIGURED_RECIPES;
    private static final LoadingCache<AlchemyRecipe, CachedElixir> CACHED_ELIXIRS;

    public static ConfiguredRecipe configuredRecipeOf(AlchemyRecipe recipe) {
        return CONFIGURED_RECIPES.getUnchecked(recipe);
    }

    public static CachedElixir cachedElixirOf(AlchemyRecipe recipe) {
        return CACHED_ELIXIRS.getUnchecked(recipe);
    }

    static {
        CONFIGURED_RECIPES = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(Duration.ofMinutes(20))
                .build(new CacheLoader<>() {

                    @Override
                    public ConfiguredRecipe load(AlchemyRecipe recipe) {
                        return ClientAlchemy.localProfile().recipeCollection()
                                .findConfig(recipe.uuid())
                                .orElseGet(recipe::configure);
                    }
                });
        CACHED_ELIXIRS = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(Duration.ofMinutes(20))
                .build(new CacheLoader<>() {

                    @Override
                    public CachedElixir load(AlchemyRecipe recipe) {
                        var configured = configuredRecipeOf(recipe);
                        var stack = ElixirumItems.ELIXIR.instantiate();
                        var processor = new BrewingProcessor(ClientAlchemy.INSTANCE, recipe);
                        ArsElixirumAPI.setElixirContents(stack, processor.brew());
                        StyleHelper.setStyle(stack, configured.getStyle());
                        StyleHelper.setChroma(stack, configured.getChroma());
                        return new CachedElixir(stack, configured);
                    }
                });
    }
}
