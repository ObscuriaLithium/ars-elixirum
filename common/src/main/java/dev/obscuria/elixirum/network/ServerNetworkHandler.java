package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.server.level.ServerPlayer;

final class ServerNetworkHandler
{
    static void handle(ServerPlayer player, ServerboundProfilePayload payload)
    {
        ServerAlchemy.handle(payload, player);
    }

    static void handle(ServerPlayer player, ServerboundCollectionActionPayload payload)
    {
        ServerAlchemy.handle(payload, player);
    }
}
