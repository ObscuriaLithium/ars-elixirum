package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.properties.AlchemyPropertyMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record S2CAlchemyMapMessage(AlchemyPropertyMap.Packed packedMap) {
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CAlchemyMapMessage> CODEC;

    public static S2CAlchemyMapMessage create(AlchemyPropertyMap.Packed packedMap) {
        return new S2CAlchemyMapMessage(packedMap);
    }

    public static S2CAlchemyMapMessage read(RegistryFriendlyByteBuf buf) {
        return new S2CAlchemyMapMessage(AlchemyPropertyMap.Packed.STREAM_CODEC.decode(buf));
    }

    public static void write(S2CAlchemyMapMessage message, RegistryFriendlyByteBuf buf) {
        AlchemyPropertyMap.Packed.STREAM_CODEC.encode(buf, message.packedMap);
    }

    static {
        CODEC = StreamCodec.composite(
                AlchemyPropertyMap.Packed.STREAM_CODEC, S2CAlchemyMapMessage::packedMap,
                S2CAlchemyMapMessage::new);
    }
}
