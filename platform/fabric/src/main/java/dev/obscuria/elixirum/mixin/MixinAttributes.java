package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Attributes.class)
public abstract class MixinAttributes {

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap$hook(Registry<Attribute> registry,
                                       CallbackInfoReturnable<Holder<Attribute>> info) {
        Elixirum.initRegistries();
    }
}
