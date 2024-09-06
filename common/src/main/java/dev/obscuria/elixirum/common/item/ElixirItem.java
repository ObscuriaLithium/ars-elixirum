package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@EssenceBlacklist
public final class ElixirItem extends Item {

    public ElixirItem() {
        super(new Properties().stacksTo(3).craftRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_STYLE.value(), ElixirStyle.DEFAULT);
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), ElixirContents.WATER);
        return stack;
    }
}
