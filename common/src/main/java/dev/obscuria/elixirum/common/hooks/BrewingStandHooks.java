package dev.obscuria.elixirum.common.hooks;

import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class BrewingStandHooks
{
    public static boolean isBrewable(NonNullList<ItemStack> items)
    {
        final var ingredient = items.get(3);
        if (ingredient.isEmpty())
        {
            return false;
        }
        else if (!ServerAlchemy.getIngredients().hasProperties(ingredient.getItem()))
        {
            return false;
        }
        else
        {
            for (var i = 0; i < 3; ++i)
            {
                final var stack = items.get(i);
                if (stack.is(Items.GLASS_BOTTLE))
                {
                    return true;
                }
            }
            return false;
        }
    }

    public static void doBrew(Level level, BlockPos pos, NonNullList<ItemStack> items)
    {
        var ingredient = items.get(3);
        final var ingredientItem = ingredient.getItem();

        final var holder = ServerAlchemy.getIngredients().getProperties(ingredientItem);
        holder.getEssences(level.holderLookup(ElixirumRegistries.ESSENCE)).forEach((essence, weight) -> {
            for (var i = 0; i < 3; ++i)
            {
                final var stack = items.get(i);
                if (!stack.is(Items.GLASS_BOTTLE)) continue;
                final var extract = ElixirumItems.EXTRACT.value().getDefaultInstance();
                extract.set(ElixirumDataComponents.EXTRACT_CONTENTS,
                        new ExtractContents(Optional.of(ingredientItem), essence, weight));
                items.set(i, extract);
                return;
            }
        });

        ingredient.shrink(1);
        final var remainingItem = ingredientItem.getCraftingRemainingItem();
        if (remainingItem != null)
        {
            final var stack = remainingItem.getDefaultInstance();
            if (ingredient.isEmpty())
            {
                ingredient = stack;
            }
            else
            {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }

        items.set(3, ingredient);
        level.levelEvent(1035, pos, 0);
    }

    public static boolean canPlaceItem(int index, ItemStack stack)
    {
        if (index == 3)
        {
            return ServerAlchemy.getIngredients().hasProperties(stack.getItem());
        }
        else if (index == 4)
        {
            return stack.is(Items.BLAZE_POWDER);
        }
        else
        {
            return stack.is(Items.GLASS_BOTTLE);
        }
    }

    public static boolean mayPlaceIngredient(ItemStack stack)
    {
        return ServerAlchemy.getIngredients().hasProperties(stack.getItem());
    }

    public static boolean mayPlacePotion(ItemStack stack)
    {
        return stack.is(Items.GLASS_BOTTLE);
    }

    public static void onTakePotion(Player player, ItemStack stack)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            ExtractContents.get(stack).ifPresent(contents ->
                    contents.source().ifPresent(source ->
                            ServerAlchemy.getProfile(serverPlayer)
                                    .discoverEssence(source, contents.essenceHolder(), true)));
        }
    }
}
