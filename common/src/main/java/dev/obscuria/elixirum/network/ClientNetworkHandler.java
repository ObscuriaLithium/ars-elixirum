package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.client.ClientAlchemy;
import net.minecraft.world.entity.player.Player;

public final class ClientNetworkHandler {

    public static void handle(ClientboundItemEssencesPacket packet, Player player) {
        ClientAlchemy.handle(packet);
    }
}
