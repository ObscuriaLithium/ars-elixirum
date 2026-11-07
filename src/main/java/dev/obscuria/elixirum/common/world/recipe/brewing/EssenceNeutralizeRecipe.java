package dev.obscuria.elixirum.common.world.recipe.brewing;

import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.api.IBrewingRecipe;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import net.minecraft.world.item.ItemStack;

public final class EssenceNeutralizeRecipe implements IBrewingRecipe {

    @Override
    public boolean isReagent(ItemStack reagent) {
        return reagent.is(ElixirumItems.EXTRACT.asItem());
    }

    @Override
    public boolean matches(ItemStack reagent, ItemStack input1, ItemStack input2, ItemStack input3) {
        if (!isReagent(reagent)) return false;
        return isValidInput(input1) || isValidInput(input2) || isValidInput(input3);
    }

    @Override
    public boolean canPlaceInPotionSlot(ItemStack input) {
        return isValidInput(input);
    }

    @Override
    public ItemStack trueResult(ItemStack reagent, ItemStack input) {
        var extract = ArsElixirumAPI.getExtractContents(reagent);
        var elixir = ArsElixirumAPI.getElixirContents(input);
        var resultElixir = elixir.subtract(extract.essences());
        var result = input.copy();
        ArsElixirumAPI.setElixirContents(result, resultElixir);
        return result;
    }

    private boolean isValidInput(ItemStack input) {
        return input.is(ElixirumItems.ELIXIR.asItem());
    }
}
