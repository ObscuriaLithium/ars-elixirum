package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.helpers.BrewingHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public abstract class MixinPotionBrewing {

    @Inject(method = "isIngredient", at = @At("RETURN"), cancellable = true)
    private static void isCustomIngredient(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!BrewingHelper.isReagent(input)) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "hasMix", at = @At("RETURN"), cancellable = true)
    private static void hasCustomMix(ItemStack input, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!BrewingHelper.hasMix(reagent, input, input, input)) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "mix", at = @At("RETURN"), cancellable = true)
    private static void customMix(ItemStack reagent, ItemStack p_potion, CallbackInfoReturnable<ItemStack> cir) {
        if (!ItemStack.matches(cir.getReturnValue(), p_potion)) return;
        var result = BrewingHelper.getTrueResult(reagent, p_potion, p_potion, p_potion, p_potion);
        if (!result.isEmpty()) cir.setReturnValue(result);
    }
}
