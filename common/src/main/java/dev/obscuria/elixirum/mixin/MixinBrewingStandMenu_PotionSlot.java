package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.registry.ElixirumItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class MixinBrewingStandMenu_PotionSlot {

    @Inject(method = "mayPlaceItem", at = @At("RETURN"), cancellable = true)
    private static void mayPlaceSolvent(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!stack.is(ElixirumItems.HONEY_SOLVENT.asItem())) return;
        cir.setReturnValue(true);
    }
}
