package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.server.ServerPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundRecipeRemoveRequest(AlchemyRecipe recipe) {

    public static void encode(ServerboundRecipeRemoveRequest request, FriendlyByteBuf byteBuf) {
        AlchemyRecipe.PAYLOAD_CODEC.write(byteBuf, request.recipe());
    }

    public static ServerboundRecipeRemoveRequest decode(FriendlyByteBuf byteBuf) {
        return new ServerboundRecipeRemoveRequest(AlchemyRecipe.PAYLOAD_CODEC.read(byteBuf));
    }

    public static void handle(ServerPlayer player, ServerboundRecipeRemoveRequest request) {
        ServerPayloadListener.handle(player, request);
    }
}
