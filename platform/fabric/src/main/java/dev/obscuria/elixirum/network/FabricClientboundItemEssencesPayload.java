package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FabricClientboundItemEssencesPayload(ClientboundItemEssencesPacket packet) implements CustomPacketPayload {
    public static final Type<FabricClientboundItemEssencesPayload> TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricClientboundItemEssencesPayload> STREAM_CODEC;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(Elixirum.key("item_essences"));
        STREAM_CODEC = StreamCodec.composite(
                ClientboundItemEssencesPacket.STREAM_CODEC,
                FabricClientboundItemEssencesPayload::packet,
                FabricClientboundItemEssencesPayload::new);
    }
}
