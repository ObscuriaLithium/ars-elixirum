package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
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

@EssenceBlacklist
public final class ElixirItem extends Item {

    public ElixirItem() {
        super(new Properties().stacksTo(3).craftRemainder(Items.GLASS_BOTTLE));
    }

    public static String getContentQuality(ElixirContents contents) {
        final var index = (int) Math.round(contents.effects().getFirst().getQuality() / 10.0);
        return Component.translatable("elixir.quality." + Math.clamp(index - 1, 1, 9)).getString();
    }

    public static String getContentName(ElixirContents contents) {
        return contents.effects().getFirst().getName().getString();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ElixirContents.get(stack).getQuality() >= 100;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public Component getName(ItemStack stack) {
        var contents = stack.getOrDefault(ElixirumDataComponents.ELIXIR_CONTENTS, ElixirContents.WATER);
        return !contents.isEmpty()
                ? Component.literal(getContentQuality(contents) + " Elixir of " + getContentName(contents))
                : super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_STYLE, ElixirStyle.DEFAULT);
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS, ElixirContents.WATER);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int tick) {
        if (tick != 32) return;
        level.playSound(null, entity, ElixirumSounds.ITEM_BOTTLE_OPEN, SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            ElixirContents.get(stack).apply(player, player, player);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }
        player.gameEvent(GameEvent.DRINK);
        return stack;
    }
}
