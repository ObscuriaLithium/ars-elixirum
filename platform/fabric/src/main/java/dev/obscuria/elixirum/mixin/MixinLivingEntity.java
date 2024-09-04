package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.registry.ElixirumAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "createLivingAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void OnCreateAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        Elixirum.initRegistries();
        info.getReturnValue().add(ElixirumAttributes.POTION_MASTERY.sourceHolder());
        info.getReturnValue().add(ElixirumAttributes.POTION_IMMUNITY.sourceHolder());
    }
}
