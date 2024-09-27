package dev.obscuria.elixirum;

import dev.obscuria.elixirum.client.hooks.ClientHooks;
import dev.obscuria.elixirum.client.sound.CauldronSoundInstance;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import org.jetbrains.annotations.ApiStatus;

public final class ElixirumClient {

    public static float getSeconds() {
        return ClientHooks.seconds;
    }

    public static void playBoilingSound(GlassCauldronEntity entity) {
        CauldronSoundInstance.play(entity);
    }

    @ApiStatus.Internal
    public static void init() {}
}
