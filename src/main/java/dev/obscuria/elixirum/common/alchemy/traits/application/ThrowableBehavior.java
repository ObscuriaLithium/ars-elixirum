package dev.obscuria.elixirum.common.alchemy.traits.application;

import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.entity.ThrownElixirProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ThrowableBehavior implements ApplicationBehavior {

    @Override
    public UseAnim getUseAnim(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity) {
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            final var tick = this.getUseDuration(stack) - timeLeft;
            if (tick < 4) return;
            if (!level.isClientSide) {
                final var projectile = new ThrownElixirProjectile(level, player);
                projectile.setItem(stack);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, Math.min(2F, (float) tick / 10), 1.0F);
                level.addFreshEntity(projectile);
            }

            player.awardStat(Stats.ITEM_USED.get(ElixirumItems.ELIXIR.asItem()));
            if (!player.getAbilities().instabuild)
                stack.shrink(1);

            level.playSound(null, entity, SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F,
                    (float) level.getRandom().triangle(0.8F, 0.1F));
        }
    }
}
