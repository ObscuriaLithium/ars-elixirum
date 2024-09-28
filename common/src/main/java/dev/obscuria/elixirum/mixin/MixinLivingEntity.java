package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.hooks.LivingEntityHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void checkTotemDeathProtection_Modify(DamageSource source,
                                                  CallbackInfoReturnable<Boolean> info) {

        if (LivingEntityHooks.checkTotemDeathProtection((LivingEntity)(Object)this, source))
            info.setReturnValue(true);
    }
}
