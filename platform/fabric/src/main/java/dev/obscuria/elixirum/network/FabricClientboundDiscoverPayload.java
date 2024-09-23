package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FabricClientboundDiscoverPayload(ClientboundDiscoverPacket packet) implements CustomPacketPayload {
    public static final Type<FabricClientboundDiscoverPayload> TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricClientboundDiscoverPayload> STREAM_CODEC;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(Elixirum.key("discover"));
        STREAM_CODEC = StreamCodec.composite(
                ClientboundDiscoverPacket.STREAM_CODEC,
                FabricClientboundDiscoverPayload::packet,
                FabricClientboundDiscoverPayload::new);
    }
}
