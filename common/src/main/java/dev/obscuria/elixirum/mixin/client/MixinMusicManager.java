package dev.obscuria.elixirum.mixin.client;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public abstract class MixinMusicManager {
    @Shadow @Nullable private SoundInstance currentMusic;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int nextSongDelay;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick_Modify(CallbackInfo info) {
        if (minecraft.screen instanceof ElixirumScreen
                && currentMusic == null
                && nextSongDelay > 0)
            nextSongDelay = 0;
    }
}
