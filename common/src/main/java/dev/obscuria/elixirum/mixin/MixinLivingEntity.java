package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void injectCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        ElixirumAttributes.injectLivingAttributes(info.getReturnValue());
    }
}
