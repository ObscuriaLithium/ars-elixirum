package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@EssenceBlacklist
public final class WitchTotemOfUndyingItem extends Item
{
    public WitchTotemOfUndyingItem()
    {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        ElixirContents.get(stack).addToTooltip(context, tooltip::add, flag);
    }
}
