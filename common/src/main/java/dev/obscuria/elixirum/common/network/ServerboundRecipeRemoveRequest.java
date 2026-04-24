package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.server.ServerPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record ServerboundRecipeRemoveRequest(UUID recipeUid) {

    public static void encode(ServerboundRecipeRemoveRequest request, FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(request.recipeUid());
    }

    public static ServerboundRecipeRemoveRequest decode(FriendlyByteBuf byteBuf) {
        return new ServerboundRecipeRemoveRequest(byteBuf.readUUID());
    }

    public static void handle(ServerPlayer player, ServerboundRecipeRemoveRequest request) {
        ServerPayloadListener.handle(player, request);
    }
}
