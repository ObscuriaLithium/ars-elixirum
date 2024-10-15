package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirTier;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

@EssenceBlacklist
public final class ElixirItem extends Item
{
    public ElixirItem()
    {
        super(new Properties()
                .stacksTo(8)
                .craftRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return ElixirTier.get(stack).isFoil();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int tick)
    {
        if (tick != 32) return;
        level.playSound(null, entity, ElixirumSounds.ITEM_BOTTLE_OPEN, SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (!(entity instanceof Player player)) return stack;

        if (player instanceof ServerPlayer serverPlayer)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            ElixirContents.get(stack).apply(player, player, player);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild)
        {
            stack.shrink(1);
            if (stack.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }
        player.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        ElixirContents.get(stack).addToTooltip(context, tooltip::add, flag);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return ElixirContents.getOptional(stack)
                .<Component>map(contents -> Component
                        .translatable("elixir.compound_name",
                                ElixirTier.get(stack).getDisplayName(),
                                super.getName(stack),
                                contents.getDisplayName()))
                .orElseGet(() -> super.getName(stack));
    }
}
