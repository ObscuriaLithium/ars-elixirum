package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.ingredient.Ingredients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundItemEssencesPacket(Ingredients.Packed packed) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundItemEssencesPacket> STREAM_CODEC;

    public static ClientboundItemEssencesPacket create(Ingredients.Packed packedMap) {
        return new ClientboundItemEssencesPacket(packedMap);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                Ingredients.Packed.STREAM_CODEC, ClientboundItemEssencesPacket::packed,
                ClientboundItemEssencesPacket::create);
    }
}
