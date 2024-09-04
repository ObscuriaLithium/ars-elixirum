package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.registry.ElixirumCustomRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public abstract class MixinBuiltInRegistries {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void OnInit(CallbackInfo ci) {
        //ElixirumCustomRegistries.init();
    }
}
