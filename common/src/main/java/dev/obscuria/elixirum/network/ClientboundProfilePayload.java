package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record ClientboundProfilePayload(ElixirumProfile.Packed content) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundProfilePayload> STREAM_CODEC;
    public static final CustomPacketPayload.Type<ClientboundProfilePayload> TYPE;

    public static ClientboundProfilePayload create(ElixirumProfile.Packed packed)
    {
        return new ClientboundProfilePayload(packed);
    }

    public static void handle(Player player, ClientboundProfilePayload payload)
    {
        ClientNetworkHandler.handle(player, payload);
    }

    @Override
    public Type<ClientboundProfilePayload> type()
    {
        return TYPE;
    }

    static
    {
        TYPE = new Type<>(Elixirum.key("clientbound_profile"));
        STREAM_CODEC = StreamCodec.composite(
                ElixirumProfile.STREAM_CODEC,
                ClientboundProfilePayload::content,
                ClientboundProfilePayload::create);
    }
}
