package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.client.ClientAlchemy;
import net.minecraft.world.entity.player.Player;

final class ClientNetworkHandler
{
    static void handle(Player player, ClientboundIngredientsPayload payload)
    {
        ClientAlchemy.handle(payload);
    }

    static void handle(Player player, ClientboundProfilePayload payload)
    {
        ClientAlchemy.handle(payload);
    }

    static void handle(Player player, ClientboundDiscoverPayload payload)
    {
        ClientAlchemy.handle(payload);
    }
}
