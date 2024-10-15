package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.hooks.BrewingStandHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BrewingStandBlockEntity.class, priority = 9999)
public abstract class MixinBrewingStandBlockEntity
{
    @Inject(method = "isBrewable", at = @At("HEAD"), cancellable = true)
    private static void isBrewable_Override(PotionBrewing potionBrewing,
                                            NonNullList<ItemStack> items,
                                            CallbackInfoReturnable<Boolean> info)
    {
        info.setReturnValue(BrewingStandHooks.isBrewable(items));
    }

    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void doBrew_Override(Level level,
                                        BlockPos pos,
                                        NonNullList<ItemStack> items,
                                        CallbackInfo info)
    {
        BrewingStandHooks.doBrew(level, pos, items);
        info.cancel();
    }

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void canPlaceItem_Override(int index,
                                       ItemStack stack,
                                       CallbackInfoReturnable<Boolean> info)
    {
        info.setReturnValue(BrewingStandHooks.canPlaceItem(index, stack));
    }
}
