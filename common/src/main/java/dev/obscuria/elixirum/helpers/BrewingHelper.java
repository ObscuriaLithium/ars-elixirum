package dev.obscuria.elixirum.helpers;

import dev.obscuria.elixirum.api.IBrewingRecipe;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@UtilityClass
public class BrewingHelper {

    public boolean isReagent(ItemStack reagent) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().isReagent(reagent)) continue;
            return true;
        }
        return false;
    }

    public boolean hasMix(ItemStack reagent, ItemStack input1, ItemStack input2, ItemStack input3) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().matches(reagent, input1, input2, input3)) continue;
            return true;
        }
        return false;
    }

    public boolean canPlaceInPotionSlot(ItemStack input) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().canPlaceInPotionSlot(input)) continue;
            return true;
        }
        return false;
    }

    public ItemStack getSpeculativeResult(
            ItemStack reagent, ItemStack input,
            ItemStack input1, ItemStack input2, ItemStack input3
    ) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().matches(reagent, input1, input2, input3)) continue;
            var result = recipe.getValue().result(reagent, input);
            if (!result.isEmpty()) return result;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getTrueResult(
            ItemStack reagent, ItemStack input,
            ItemStack input1, ItemStack input2, ItemStack input3
    ) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().matches(reagent, input1, input2, input3)) continue;
            var result = recipe.getValue().trueResult(reagent, input);
            if (!result.isEmpty()) return result;
        }
        return ItemStack.EMPTY;
    }

    public Optional<IBrewingRecipe> findMatch(ItemStack reagent, ItemStack input1, ItemStack input2, ItemStack input3) {
        for (var recipe : ElixirumRegistries.BREWING_RECIPE.entrySet()) {
            if (!recipe.getValue().matches(reagent, input1, input2, input3)) continue;
            return Optional.of(recipe.getValue());
        }
        return Optional.empty();
    }
}
