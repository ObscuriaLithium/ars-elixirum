package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.basics.ExtractContents;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PotionBrewing.class)
public abstract class MixinPotionBrewing {

    @Inject(method = "isIngredient", at = @At("RETURN"), cancellable = true)
    private static void isAlchemyIngredient(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (Alchemy.guess().ingredients().propertiesOf(input).isEmpty()) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "hasMix", at = @At("RETURN"), cancellable = true)
    private static void hasAlchemyMix(ItemStack input, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!input.is(ElixirumItems.HONEY_SOLVENT.asItem())) return;
        if (Alchemy.guess().ingredients().propertiesOf(reagent).isEmpty()) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "mix", at = @At("RETURN"), cancellable = true)
    private static void alchemyMix(ItemStack reagent, ItemStack p_potion, CallbackInfoReturnable<ItemStack> cir) {
        if (!p_potion.is(ElixirumItems.HONEY_SOLVENT.asItem())) return;
        var properties = Alchemy.guess().ingredients().propertiesOf(reagent);
        if (properties.isEmpty()) return;

        var result = ElixirumItems.EXTRACT.instantiate();
        if (ItemStackCache.isBrewFlag(reagent)) {
            var essences = new ArrayList<>(properties.essences().sorted());
            for (var suppressed : ItemStackCache.suppressedEssences(reagent)) {
                essences.removeIf(it -> it.getKey().equals(suppressed));
            }
            if (essences.isEmpty()) return;

            Collections.shuffle(essences);
            var extracted = essences.get(RandomUtils.nextInt(0, essences.size()));
            var map = EssenceHolderMap.single(extracted.getKey(), extracted.getIntValue());
            ArsElixirumHelper.setExtractContents(result, new ExtractContents(reagent.getItem(), map));
            ItemStackCache.suppressedEssences(reagent).add(extracted.getKey());
        } else {
            ArsElixirumHelper.setExtractContents(result, new ExtractContents(reagent.getItem(), properties.essences()));
        }

        cir.setReturnValue(result);
    }
}
