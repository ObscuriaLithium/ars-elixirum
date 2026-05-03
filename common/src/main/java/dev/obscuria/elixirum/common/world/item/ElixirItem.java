package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
        @Nullable var customName = ArsElixirumAPI.getCustomName(stack).orElse(null);
        if (customName != null) return customName.text();
        return ArsElixirumAPI.getElixirContents(stack).form().makeElixirName(stack);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        @Nullable var customLore = ArsElixirumAPI.getCustomLore(stack).orElse(null);
        if (customLore != null) return Optional.empty();
        return Optional.of(new ElixirContentsTooltip(stack, ArsElixirumAPI.getElixirContents(stack)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        @Nullable var customLore = ArsElixirumAPI.getCustomLore(stack).orElse(null);
        if (customLore == null) return;
        components.addAll(Tooltips.process(customLore.text(), this, TooltipOptions.LORE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return ArsElixirumAPI.getElixirContents(stack).form().getUseAnim(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return ArsElixirumAPI.getElixirContents(stack).form().getUseDuration(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return ArsElixirumAPI.getElixirContents(stack).form().finishUsing(stack, level, entity);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        ArsElixirumAPI.getElixirContents(stack).form().releaseUsing(stack, level, entity, timeLeft);
    }
}