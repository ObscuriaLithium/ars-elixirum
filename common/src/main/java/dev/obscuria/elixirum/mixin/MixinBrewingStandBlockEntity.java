package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.world.ItemStackCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity {

    @Inject(method = "doBrew", at = @At("HEAD"))
    private static void beforeBrew(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
        var reagent = items.get(3);
        ItemStackCache.suppressedEssences(reagent).clear();
        ItemStackCache.setBrewFlag(reagent, true);
    }

    @Inject(method = "doBrew", at = @At("RETURN"))
    private static void afterBrew(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
        var reagent = items.get(3);
        ItemStackCache.suppressedEssences(reagent).clear();
        ItemStackCache.setBrewFlag(reagent, false);
    }
}