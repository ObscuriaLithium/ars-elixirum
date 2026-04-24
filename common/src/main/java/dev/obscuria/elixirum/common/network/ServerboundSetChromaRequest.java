package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.server.ServerPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record ServerboundSetChromaRequest(UUID recipeUid, Chroma chroma) {

    public static void encode(ServerboundSetChromaRequest request, FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(request.recipeUid());
        byteBuf.writeInt(request.chroma().id);
    }

    public static ServerboundSetChromaRequest decode(FriendlyByteBuf byteBuf) {
        return new ServerboundSetChromaRequest(byteBuf.readUUID(), Chroma.byId(byteBuf.readInt()));
    }

    public static void handle(ServerPlayer player, ServerboundSetChromaRequest request) {
        ServerPayloadListener.handle(player, request);
    }
}
