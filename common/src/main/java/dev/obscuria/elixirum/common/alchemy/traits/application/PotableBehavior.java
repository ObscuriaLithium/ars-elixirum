package dev.obscuria.elixirum.common.alchemy.traits.application;

import dev.obscuria.elixirum.helpers.ContentsHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotableBehavior implements ApplicationBehavior {

    @Override
    public UseAnim getUseAnim(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;

        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            ContentsHelper.elixir(stack).apply(entity, entity, entity);
        }

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
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

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {}
}
