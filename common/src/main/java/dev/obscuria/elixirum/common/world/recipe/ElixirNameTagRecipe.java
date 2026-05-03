package dev.obscuria.elixirum.common.world.recipe;

import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.api.alchemy.components.CustomText;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.registry.ElixirumRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class ElixirNameTagRecipe extends CustomRecipe {

    public ElixirNameTagRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        var totalElixirs = 0;
        var totalNameTags = 0;

        for (var stack : container.getItems()) {
            if (stack.isEmpty()) continue;
            if (stack.is(ElixirumItems.ELIXIR.asItem())) {
                totalElixirs += 1;
                continue;
            } else if (stack.is(Items.NAME_TAG)) {
                totalNameTags++;
                continue;
            }
            return false;
        }

        return totalElixirs == 1 && totalNameTags > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        var result = ItemStack.EMPTY;
        var elixirIndex = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            var stack = container.getItem(i);
            if (!stack.is(ElixirumItems.ELIXIR.asItem())) continue;
            elixirIndex = i;
            result = stack.copy();
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            var stack = container.getItem(i);
            if (!stack.is(Items.NAME_TAG)) continue;
            if (i < elixirIndex) {
                ArsElixirumAPI.setCustomName(result, CustomText.of(stack.getHoverName()));
            } else {
                ArsElixirumAPI.setCustomLore(result, CustomText.of(stack.getHoverName()));
            }
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width + height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ElixirumRecipeSerializers.ELIXIR_NAME_TAG;
    }
}
