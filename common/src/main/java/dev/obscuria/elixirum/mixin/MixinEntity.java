package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.registry.ElixirumMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @ModifyVariable(
            method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 3)
    private float playSound$modifyPitch(float pitch) {
        if (elixirum$asEntity() instanceof LivingEntity living) {
            var shrink = living.getEffect(ElixirumMobEffects.SHRINK.holder());
            if (shrink != null) return Math.clamp(pitch * (1F + 0.1F * (1 + shrink.getAmplifier())), 0F, 2F);
            var grow = living.getEffect(ElixirumMobEffects.GROW.holder());
            if (grow != null) return Math.clamp(pitch * (1F - 0.05F * (1 + grow.getAmplifier())), 0F, 2F);
        }
        return pitch;
    }

    @Unique
    private Entity elixirum$asEntity() {
        return (Entity) (Object) this;
    }
}
