package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.elixirum.common.world.tooltip.EffectsTooltip;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Optional;

public class ElixirItem extends Item {

    public ElixirItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ElixirQuality.of(stack).isFoil;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return ElixirQuality.of(stack).rarity;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ElixirQuality.makeElixirName(stack, super.getName(stack));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new EffectsTooltip(stack, ArsElixirumHelper.getElixirContents(stack)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;

        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            ArsElixirumHelper.getElixirContents(stack).apply(entity, entity, entity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.gameEvent(GameEvent.DRINK);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            var result = Items.GLASS_BOTTLE.getDefaultInstance();
            if (stack.isEmpty()) return result;
            if (player.addItem(result)) return stack;
            player.drop(result, false);
        }

        return stack;
    }
}
