package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ElixirItem extends Item {

    public ElixirItem(Properties properties) {
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
    public Component getName(ItemStack stack) {
        return ContentsHelper.elixir(stack).form().makeElixirName(stack);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ElixirContentsTooltip(stack, ContentsHelper.elixir(stack)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return ContentsHelper.elixir(stack).form().getUseAnim(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return ContentsHelper.elixir(stack).form().getUseDuration(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return ContentsHelper.elixir(stack).form().finishUsing(stack, level, entity);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        ContentsHelper.elixir(stack).form().releaseUsing(stack, level, entity, timeLeft);
    }
}