package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record FabricS2CAlchemyMapPayload(S2CAlchemyMapMessage message) implements CustomPacketPayload {
    public static final Type<FabricS2CAlchemyMapPayload> TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricS2CAlchemyMapPayload> CODEC;

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(Elixirum.key("alchemy_map"));
        CODEC = StreamCodec.composite(
                S2CAlchemyMapMessage.CODEC, FabricS2CAlchemyMapPayload::message,
                FabricS2CAlchemyMapPayload::new);
    }
}
