package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.hooks._LivingEntityHooks;
import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void injectLivingAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        ElixirumAttributes.injectLivingAttributes(info.getReturnValue());
    }

    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    private void trackEffectKnowledge(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
        var self = (LivingEntity) (Object) this;
        _LivingEntityHooks.trackEffectKnowledge(self, effectInstance);
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void injectWitchTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        var self = (LivingEntity) (Object) this;
        if (!_LivingEntityHooks.checkWitchTotemDeathProtection(self, damageSource)) return;
        info.setReturnValue(true);
    }
}
