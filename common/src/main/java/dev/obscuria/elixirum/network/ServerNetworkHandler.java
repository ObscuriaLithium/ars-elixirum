package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ServerNetworkHandler {

    public static void handle(ServerboundProfilePacket packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer)
            ServerAlchemy.handle(packet, serverPlayer);
    }

    public static void handle(ServerboundCollectionActionPacket packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer)
            ServerAlchemy.handle(packet, serverPlayer);
    }
}
