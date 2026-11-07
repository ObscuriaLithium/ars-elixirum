package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.client.ClientPayloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientboundElixirBrewedPayload(AlchemyRecipe recipe) {

    public static void encode(ClientboundElixirBrewedPayload payload, FriendlyByteBuf byteBuf) {
        AlchemyRecipe.payloadCodec().write(byteBuf, payload.recipe());
    }

    public static ClientboundElixirBrewedPayload decode(FriendlyByteBuf byteBuf) {
        return new ClientboundElixirBrewedPayload(AlchemyRecipe.payloadCodec().read(byteBuf));
    }

    public static void handle(Player player, ClientboundElixirBrewedPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }
}