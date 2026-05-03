package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.server.ServerPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record ServerboundSetStyleRequest(UUID recipeUid, StyleVariant style) {

    public static void encode(ServerboundSetStyleRequest request, FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(request.recipeUid());
        StyleVariant.payloadCodec().write(byteBuf, request.style());
    }

    public static ServerboundSetStyleRequest decode(FriendlyByteBuf byteBuf) {
        return new ServerboundSetStyleRequest(
                byteBuf.readUUID(),
                StyleVariant.payloadCodec().read(byteBuf));
    }

    public static void handle(ServerPlayer player, ServerboundSetStyleRequest request) {
        ServerPayloadListener.handle(player, request);
    }
}
