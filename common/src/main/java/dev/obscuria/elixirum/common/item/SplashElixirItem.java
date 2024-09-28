package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirTier;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.common.entity.ThrownElixirProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

@EssenceBlacklist
public final class SplashElixirItem extends Item {

    public SplashElixirItem() {
        super(new Properties()
                .stacksTo(8)
                .craftRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ElixirTier.get(stack).isFoil();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingTicks) {
        if (entity instanceof Player player) {
            final var tick = this.getUseDuration(stack, entity) - remainingTicks;
            if (tick < 4) return;
            if (!level.isClientSide) {
                final var projectile = new ThrownElixirProjectile(level, player);
                projectile.setItem(stack);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, Math.min(2F, (float) tick / 10), 1.0F);
                level.addFreshEntity(projectile);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild)
                stack.shrink(1);

            level.playSound(null, entity, SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F,
                    (float) level.getRandom().triangle(0.8F, 0.1F));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ElixirContents.get(stack).addToTooltip(context, tooltip::add, flag);
    }

    @Override
    public Component getName(ItemStack stack) {
        return ElixirContents.getOptional(stack)
                .<Component>map(contents -> Component
                        .translatable("elixir.compound_name",
                                ElixirTier.get(stack).getDisplayName(),
                                super.getName(stack),
                                contents.getDisplayName()))
                .orElseGet(() -> super.getName(stack));
    }
}
