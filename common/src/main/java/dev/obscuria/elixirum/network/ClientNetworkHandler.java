package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.client.ClientAlchemy;
import net.minecraft.client.player.LocalPlayer;

public final class ClientNetworkHandler {

    public static void handle(S2CAlchemyMapMessage message, LocalPlayer player) {
        ClientAlchemy.handle(message);
    }
}
