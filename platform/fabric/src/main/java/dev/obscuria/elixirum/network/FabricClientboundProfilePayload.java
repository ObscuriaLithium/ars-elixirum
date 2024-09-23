package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FabricClientboundProfilePayload(ClientboundProfilePacket packet) implements CustomPacketPayload {
    public static final Type<FabricClientboundProfilePayload> TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricClientboundProfilePayload> STREAM_CODEC;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(Elixirum.key("profile"));
        STREAM_CODEC = StreamCodec.composite(
                ClientboundProfilePacket.STREAM_CODEC,
                FabricClientboundProfilePayload::packet,
                FabricClientboundProfilePayload::new);
    }
}
