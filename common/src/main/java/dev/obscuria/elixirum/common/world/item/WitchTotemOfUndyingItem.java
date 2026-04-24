package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WitchTotemOfUndyingItem extends Item {

    public WitchTotemOfUndyingItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ElixirQuality.fromStack(stack).isFoil();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return ElixirQuality.fromStack(stack).getRarity();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ElixirContentsTooltip(stack, ContentsHelper.elixir(stack)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        //stack.getElixirContents().addToTooltip(context, tooltip::add, flag);
    }
}
