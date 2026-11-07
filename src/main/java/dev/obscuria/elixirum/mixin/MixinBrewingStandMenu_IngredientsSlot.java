package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.helpers.BrewingHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public abstract class MixinBrewingStandMenu_IngredientsSlot {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void mayPlaceCustomReagent(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!BrewingHelper.isReagent(stack)) return;
        cir.setReturnValue(true);
    }
}
