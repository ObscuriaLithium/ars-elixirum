package dev.obscuria.elixirum.mixin.client;

import dev.obscuria.elixirum.client.KeyMappings;
import dev.obscuria.elixirum.client.sounds.CauldronSoundInstance;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "tick", at = @At("HEAD"))
    private void customTick(CallbackInfo info) {
        CauldronSoundInstance.tickTracked();
        if (KeyMappings.COLLECTION.consumeClick()) {
            KeyMappings.collectionPressed();
        }
    }
}
