package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.client.ClientPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientboundMasterySyncPayload(int masteryLevel, int masteryXp) {

    public static void encode(ClientboundMasterySyncPayload payload, FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(payload.masteryLevel());
        byteBuf.writeInt(payload.masteryXp());
    }

    public static ClientboundMasterySyncPayload decode(FriendlyByteBuf byteBuf) {
        return new ClientboundMasterySyncPayload(
                byteBuf.readInt(),
                byteBuf.readInt());
    }

    public static void handle(Player player, ClientboundMasterySyncPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }
}
