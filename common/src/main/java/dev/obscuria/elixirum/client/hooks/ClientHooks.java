package dev.obscuria.elixirum.client.hooks;

import dev.obscuria.core.api.v1.client.ObscureClientEvents;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.ElixirumKeyMappings;
import dev.obscuria.elixirum.client.sound.CauldronSoundInstance;

public final class ClientHooks
{
    private static final long START_TIME = System.currentTimeMillis();
    public static float seconds;

    public static void init()
    {
        ObscureClientEvents.START_RENDER.register(() ->
                seconds = (System.currentTimeMillis() - START_TIME) / 1000f);

        ObscureClientEvents.START_CLIENT_TICK.register(minecraft -> {
            CauldronSoundInstance.onClientTick();
            while (ElixirumKeyMappings.MENU.consumeClick())
                ElixirumKeyMappings.menuPressed(minecraft);
        });
    }

    public static void onDisconnect()
    {
        ClientAlchemy.clearCache();
    }
}
