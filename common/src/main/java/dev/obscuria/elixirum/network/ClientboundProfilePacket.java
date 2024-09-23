package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundProfilePacket(ElixirumProfile.Packed content) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundProfilePacket> STREAM_CODEC;

    public static ClientboundProfilePacket create(ElixirumProfile.Packed packed) {
        return new ClientboundProfilePacket(packed);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ElixirumProfile.STREAM_CODEC, ClientboundProfilePacket::content,
                ClientboundProfilePacket::create);
    }
}
