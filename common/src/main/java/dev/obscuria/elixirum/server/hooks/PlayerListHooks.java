package dev.obscuria.elixirum.server.hooks;

import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerListHooks
{
    static void playerJoined(ServerPlayer player)
    {
        ServerAlchemy.registerPlayer(player);
    }

    static void playerLeaved(ServerPlayer player)
    {
        ServerAlchemy.unregisterPlayer(player);
    }
}
