package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundProfilePayload(ElixirumProfile.Packed content) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundProfilePayload> STREAM_CODEC;
    public static final CustomPacketPayload.Type<ServerboundProfilePayload> TYPE;

    public static ServerboundProfilePayload create(ElixirumProfile.Packed packed)
    {
        return new ServerboundProfilePayload(packed);
    }

    public static void handle(ServerPlayer player, ServerboundProfilePayload payload)
    {
        ServerNetworkHandler.handle(player, payload);
    }

    @Override
    public Type<ServerboundProfilePayload> type()
    {
        return TYPE;
    }

    static
    {
        TYPE = new Type<>(Elixirum.key("serverbound_profile"));
        STREAM_CODEC = StreamCodec.composite(
                ElixirumProfile.STREAM_CODEC,
                ServerboundProfilePayload::content,
                ServerboundProfilePayload::create);
    }
}
