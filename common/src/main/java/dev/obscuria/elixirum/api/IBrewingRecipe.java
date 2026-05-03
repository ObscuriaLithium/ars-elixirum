package dev.obscuria.elixirum.api;

import dev.obscuria.elixirum.common.world.recipe.brewing.EssenceExtractRecipe;
import dev.obscuria.elixirum.common.world.recipe.brewing.EssenceNeutralizeRecipe;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import net.minecraft.world.item.ItemStack;

public interface IBrewingRecipe {

    boolean isReagent(ItemStack reagent);

    boolean matches(ItemStack reagent, ItemStack input1, ItemStack input2, ItemStack input3);

    boolean canPlaceInPotionSlot(ItemStack input);

    ItemStack trueResult(ItemStack reagent, ItemStack input);

    default ItemStack result(ItemStack reagent, ItemStack input) {
        return trueResult(reagent, input);
    }

    static void bootstrap(BootstrapContext<IBrewingRecipe> context) {
        context.register("essence_extract", EssenceExtractRecipe::new);
        context.register("essence_neutralize", EssenceNeutralizeRecipe::new);
    }
}
