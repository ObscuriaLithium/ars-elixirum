package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import dev.obscuria.elixirum.server.ServerPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record ServerboundSetStyleRequest(UUID recipe, StyleVariant style) {

    public static void encode(ServerboundSetStyleRequest request, FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(request.recipe());
        StyleVariant.PAYLOAD_CODEC.write(byteBuf, request.style());
    }

    public static ServerboundSetStyleRequest decode(FriendlyByteBuf byteBuf) {
        return new ServerboundSetStyleRequest(
                byteBuf.readUUID(),
                StyleVariant.PAYLOAD_CODEC.read(byteBuf));
    }

    public static void handle(ServerPlayer player, ServerboundSetStyleRequest request) {
        ServerPayloadListener.handle(player, request);
    }
}
