package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record NeoClientboundItemEssencesPayload(ClientboundItemEssencesPacket packet) implements CustomPacketPayload {
    public static final Type<NeoClientboundItemEssencesPayload> TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, NeoClientboundItemEssencesPayload> STREAM_CODEC;

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(Elixirum.key("alchemy_map"));
        STREAM_CODEC = StreamCodec.composite(
                ClientboundItemEssencesPacket.STREAM_CODEC,
                NeoClientboundItemEssencesPayload::packet,
                NeoClientboundItemEssencesPayload::new);
    }
}
