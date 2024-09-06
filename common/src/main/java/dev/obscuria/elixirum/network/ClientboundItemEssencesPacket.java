package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.essence.ItemEssenceMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundItemEssencesPacket(ItemEssenceMap.Packed map) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundItemEssencesPacket> STREAM_CODEC;

    public static ClientboundItemEssencesPacket create(ItemEssenceMap.Packed packedMap) {
        return new ClientboundItemEssencesPacket(packedMap);
    }

    public static void encode(ClientboundItemEssencesPacket message, RegistryFriendlyByteBuf buf) {
        ItemEssenceMap.Packed.STREAM_CODEC.encode(buf, message.map);
    }

    public static ClientboundItemEssencesPacket decode(RegistryFriendlyByteBuf buf) {
        return new ClientboundItemEssencesPacket(ItemEssenceMap.Packed.STREAM_CODEC.decode(buf));
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ItemEssenceMap.Packed.STREAM_CODEC, ClientboundItemEssencesPacket::map,
                ClientboundItemEssencesPacket::new);
    }
}
