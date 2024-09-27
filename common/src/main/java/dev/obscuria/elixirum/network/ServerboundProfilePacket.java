package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ServerboundProfilePacket(ElixirumProfile.Packed content) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundProfilePacket> STREAM_CODEC;

    public static ServerboundProfilePacket create(ElixirumProfile.Packed packed) {
        return new ServerboundProfilePacket(packed);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ElixirumProfile.STREAM_CODEC,
                ServerboundProfilePacket::content,
                ServerboundProfilePacket::create);
    }
}
