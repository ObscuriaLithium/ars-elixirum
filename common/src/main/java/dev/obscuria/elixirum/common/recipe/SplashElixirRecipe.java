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

public final class SplashElixirRecipe extends CustomRecipe {

    public SplashElixirRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        var elixirs = 0;
        var gunPowders = 0;
        var bottles = 0;
        for (var stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (stack.is(ElixirumItems.ELIXIR.asItem())) {
                elixirs += 1;
                continue;
            } else if (stack.is(Items.GUNPOWDER)) {
                gunPowders += 1;
                continue;
            } else if (stack.is(Items.GLASS_BOTTLE)) {
                bottles += 1;
                continue;
            }
            return false;
        }
        return elixirs == 1 && gunPowders == 1 && bottles >= 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        final var bottles = (int) input.items().stream()
                .filter(stack -> stack.is(Items.GLASS_BOTTLE))
                .count();
        final var contents = ElixirContents.get(input.items().stream()
                .filter(stack -> stack.is(ElixirumItems.ELIXIR.asItem()))
                .findFirst().orElse(ItemStack.EMPTY));
        final var result = new ItemStack(ElixirumItems.SPLASH_ELIXIR.asItem(), bottles);
        return contents.split(bottles).set(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width + height >= 3;
    }

    @Override
    public RecipeSerializer<SplashElixirRecipe> getSerializer() {
        return ElixirumRecipeSerializers.SPLASH_ELIXIR;
    }
}
