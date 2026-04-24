package dev.obscuria.elixirum.common.alchemy.traits.application;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public interface ApplicationBehavior {

    UseAnim getUseAnim(ItemStack stack);

    int getUseDuration(ItemStack stack);

    ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity);

    void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft);
}
