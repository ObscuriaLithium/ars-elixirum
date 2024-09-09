package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.essence.ItemEssences;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundItemEssencesPacket(ItemEssences.Packed packed) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundItemEssencesPacket> STREAM_CODEC;

    public static ClientboundItemEssencesPacket create(ItemEssences.Packed packedMap) {
        return new ClientboundItemEssencesPacket(packedMap);
    }

    public static void encode(ClientboundItemEssencesPacket message, RegistryFriendlyByteBuf buf) {
        ItemEssences.Packed.STREAM_CODEC.encode(buf, message.packed);
    }

    public static ClientboundItemEssencesPacket decode(RegistryFriendlyByteBuf buf) {
        return new ClientboundItemEssencesPacket(ItemEssences.Packed.STREAM_CODEC.decode(buf));
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ItemEssences.Packed.STREAM_CODEC, ClientboundItemEssencesPacket::packed,
                ClientboundItemEssencesPacket::new);
    }
}
