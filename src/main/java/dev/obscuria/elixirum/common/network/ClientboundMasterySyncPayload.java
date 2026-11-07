package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.api.codex.profile.AlchemyMastery;
import dev.obscuria.elixirum.client.ClientPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientboundMasterySyncPayload(int level, int xp) {

    public ClientboundMasterySyncPayload(AlchemyMastery mastery) {
        this(mastery.level(), mastery.xp());
    }

    public static void encode(ClientboundMasterySyncPayload payload, FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(payload.level());
        byteBuf.writeInt(payload.xp());
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
