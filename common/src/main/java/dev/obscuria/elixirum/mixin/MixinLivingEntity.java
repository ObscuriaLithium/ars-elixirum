package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void injectCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        ElixirumAttributes.injectLivingAttributes(info.getReturnValue());
    }

    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    private void onEffectAdded(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
        var self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        var profile = Alchemy.get(player.level()).profileOf(player);
        profile.knowledge().effectApplied(effectInstance.getEffect());
    }
}
