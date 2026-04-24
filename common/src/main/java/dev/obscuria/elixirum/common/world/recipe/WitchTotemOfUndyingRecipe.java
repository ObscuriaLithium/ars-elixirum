package dev.obscuria.elixirum.common.world.recipe;

import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.registry.ElixirumRecipeSerializers;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class WitchTotemOfUndyingRecipe extends CustomRecipe {

    public WitchTotemOfUndyingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        var elixirs = 0;
        var totems = 0;
        for (var stack : container.getItems()) {
            if (stack.isEmpty()) continue;
            if (stack.is(ElixirumItems.ELIXIR.asItem())) {
                elixirs += 1;
                continue;
            } else if (stack.is(Items.TOTEM_OF_UNDYING)) {
                totems++;
                continue;
            }
            return false;
        }
        return elixirs == 1 && totems == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        var contents = ContentsHelper.elixir(container.getItems().stream()
                .filter(stack -> stack.is(ElixirumItems.ELIXIR.asItem()))
                .findFirst().orElse(ItemStack.EMPTY));
        var result = new ItemStack(ElixirumItems.WITCH_TOTEM_OF_UNDYING.asItem());
        ContentsHelper.setElixir(result, contents);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width + height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ElixirumRecipeSerializers.WITCH_TOTEM_OF_UNDYING;
    }
}
