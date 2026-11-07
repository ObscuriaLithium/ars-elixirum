package dev.obscuria.elixirum.platform.fabric.mixin;

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
        ElixirumAttributes.addTo(cir.getReturnValue());
    }
}
