package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.client.ClientPayloadListener;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientboundElixirBrewedPayload(AlchemyRecipe recipe) {

    public static void encode(ClientboundElixirBrewedPayload payload, FriendlyByteBuf byteBuf) {
        AlchemyRecipe.PAYLOAD_CODEC.write(byteBuf, payload.recipe());
    }

    public static ClientboundElixirBrewedPayload decode(FriendlyByteBuf byteBuf) {
        return new ClientboundElixirBrewedPayload(AlchemyRecipe.PAYLOAD_CODEC.read(byteBuf));
    }

    public static void handle(Player player, ClientboundElixirBrewedPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }
}