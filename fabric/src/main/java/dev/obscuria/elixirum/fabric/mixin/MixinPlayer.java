package dev.obscuria.elixirum.fabric.mixin;

import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void injectCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue()
                .add(ElixirumAttributes.POTION_MASTERY)
                .add(ElixirumAttributes.POTION_RESISTANCE)
                .add(ElixirumAttributes.CORPUS_MASTERY)
                .add(ElixirumAttributes.CORPUS_RESISTANCE)
                .add(ElixirumAttributes.ANIMA_MASTERY)
                .add(ElixirumAttributes.ANIMA_RESISTANCE)
                .add(ElixirumAttributes.VENENUM_MASTERY)
                .add(ElixirumAttributes.VENENUM_RESISTANCE)
                .add(ElixirumAttributes.MEDELA_MASTERY)
                .add(ElixirumAttributes.MEDELA_RESISTANCE)
                .add(ElixirumAttributes.CRESCERE_MASTERY)
                .add(ElixirumAttributes.CRESCERE_RESISTANCE)
                .add(ElixirumAttributes.MUTATIO_MASTERY)
                .add(ElixirumAttributes.MUTATIO_RESISTANCE)
                .add(ElixirumAttributes.FORTUNA_MASTERY)
                .add(ElixirumAttributes.FORTUNA_RESISTANCE)
                .add(ElixirumAttributes.TENEBRAE_MASTERY)
                .add(ElixirumAttributes.TENEBRAE_RESISTANCE);
    }
}
