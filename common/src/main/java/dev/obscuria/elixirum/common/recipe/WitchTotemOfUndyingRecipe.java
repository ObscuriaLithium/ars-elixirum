package dev.obscuria.elixirum.common.recipe;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class WitchTotemOfUndyingRecipe extends CustomRecipe {

    public WitchTotemOfUndyingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        var elixirs = 0;
        var totems = 0;
        for (var stack : input.items()) {
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        final var contents = ElixirContents.get(input.items().stream()
                .filter(stack -> stack.is(ElixirumItems.ELIXIR.asItem()))
                .findFirst().orElse(ItemStack.EMPTY));
        final var result = new ItemStack(ElixirumItems.WITCH_TOTEM_OF_UNDYING.asItem());
        return contents.set(result);
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
