package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record ClientboundDiscoverPacket(Item item, Holder<Essence> essence) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDiscoverPacket> STREAM_CODEC;

    public static ClientboundDiscoverPacket create(Item item, Holder<Essence> essence) {
        return new ClientboundDiscoverPacket(item, essence);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM), ClientboundDiscoverPacket::item,
                Essence.STREAM_CODEC, ClientboundDiscoverPacket::essence,
                ClientboundDiscoverPacket::create);
    }
}
