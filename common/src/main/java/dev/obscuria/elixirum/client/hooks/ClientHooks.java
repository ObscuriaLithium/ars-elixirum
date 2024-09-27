package dev.obscuria.elixirum.client.hooks;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.sound.CauldronSoundInstance;

public final class ClientHooks {
    private static final long START_TIME = System.currentTimeMillis();
    public static float seconds;

    public static void onClientTick() {
        CauldronSoundInstance.onClientTick();
    }

    public static void onRenderTick() {
        seconds = (System.currentTimeMillis() - START_TIME) / 1000f;
    }

    public static void onDisconnect() {
        ClientAlchemy.clearCache();
    }
}
