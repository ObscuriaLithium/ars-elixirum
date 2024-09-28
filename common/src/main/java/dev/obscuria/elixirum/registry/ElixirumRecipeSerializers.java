package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.recipe.SplashElixirRecipe;
import dev.obscuria.elixirum.common.recipe.WitchTotemOfUndyingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public interface ElixirumRecipeSerializers {
    RecipeSerializer<SplashElixirRecipe> SPLASH_ELIXIR = simple("splash_elixir", SplashElixirRecipe::new);
    RecipeSerializer<WitchTotemOfUndyingRecipe> WITCH_TOTEM_OF_UNDYING = simple("witch_totem_of_undying", WitchTotemOfUndyingRecipe::new);

    private static <T extends CraftingRecipe> RecipeSerializer<T>
    simple(final String name, SimpleCraftingRecipeSerializer.Factory<T> factory) {
        final var value = new SimpleCraftingRecipeSerializer<>(factory);
        Elixirum.PLATFORM.registerReference(BuiltInRegistries.RECIPE_SERIALIZER, Elixirum.key(name), () -> value);
        return value;
    }

    static void setup() {}
}
