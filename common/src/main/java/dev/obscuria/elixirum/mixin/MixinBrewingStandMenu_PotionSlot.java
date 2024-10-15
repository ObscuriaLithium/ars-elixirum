package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.hooks.BrewingStandHooks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public abstract class MixinBrewingStandMenu_PotionSlot
{
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void mayPlace_Override(ItemStack stack,
                                   CallbackInfoReturnable<Boolean> info)
    {
        info.setReturnValue(BrewingStandHooks.mayPlacePotion(stack));
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTake_Listener(Player player,
                                 ItemStack stack,
                                 CallbackInfo info)
    {
        BrewingStandHooks.onTakePotion(player, stack);
    }
}
