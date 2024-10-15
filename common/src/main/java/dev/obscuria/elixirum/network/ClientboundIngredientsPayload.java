package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ingredient.Ingredients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record ClientboundIngredientsPayload(Ingredients.Packed packed) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundIngredientsPayload> STREAM_CODEC;
    public static final CustomPacketPayload.Type<ClientboundIngredientsPayload> TYPE;

    public static ClientboundIngredientsPayload create(Ingredients.Packed packedMap)
    {
        return new ClientboundIngredientsPayload(packedMap);
    }

    public static void handle(Player player, ClientboundIngredientsPayload payload)
    {
        ClientNetworkHandler.handle(player, payload);
    }

    @Override
    public Type<ClientboundIngredientsPayload> type()
    {
        return TYPE;
    }

    static
    {
        TYPE = new Type<>(Elixirum.key("clientbound_ingredients"));
        STREAM_CODEC = StreamCodec.composite(
                Ingredients.Packed.STREAM_CODEC, ClientboundIngredientsPayload::packed,
                ClientboundIngredientsPayload::create);
    }
}
