package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.client.ClientPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientboundMasteryLevelUpPayload(int masteryLevel) {

    public static void encode(ClientboundMasteryLevelUpPayload payload, FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(payload.masteryLevel());
    }

    public static ClientboundMasteryLevelUpPayload decode(FriendlyByteBuf byteBuf) {
        return new ClientboundMasteryLevelUpPayload(byteBuf.readInt());
    }

    public static void handle(Player player, ClientboundMasteryLevelUpPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }
}
