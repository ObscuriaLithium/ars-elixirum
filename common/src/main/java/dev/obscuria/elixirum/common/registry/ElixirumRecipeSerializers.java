package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.recipe.WitchTotemOfUndyingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public interface ElixirumRecipeSerializers {

    RecipeSerializer<WitchTotemOfUndyingRecipe> WITCH_TOTEM_OF_UNDYING = simple("witch_totem_of_undying", WitchTotemOfUndyingRecipe::new);

    private static <T extends CraftingRecipe> RecipeSerializer<T> simple(
            String name,
            SimpleCraftingRecipeSerializer.Factory<T> factory
    ) {
        final var value = new SimpleCraftingRecipeSerializer<>(factory);
        ElixirumRegistries.REGISTRAR.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                ArsElixirum.identifier(name),
                () -> value);
        return value;
    }

    static void init() {}
}
