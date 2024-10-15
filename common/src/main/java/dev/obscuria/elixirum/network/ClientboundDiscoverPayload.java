package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record ClientboundDiscoverPayload(Item item, Holder<Essence> essence) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDiscoverPayload> STREAM_CODEC;
    public static final Type<ClientboundDiscoverPayload> TYPE;

    public static ClientboundDiscoverPayload create(Item item, Holder<Essence> essence)
    {
        return new ClientboundDiscoverPayload(item, essence);
    }

    public static void handle(Player player, ClientboundDiscoverPayload payload)
    {
        ClientNetworkHandler.handle(player, payload);
    }

    @Override
    public Type<ClientboundDiscoverPayload> type()
    {
        return TYPE;
    }

    static
    {
        TYPE = new Type<>(Elixirum.key("clientbound_discover"));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM), ClientboundDiscoverPayload::item,
                Essence.STREAM_CODEC, ClientboundDiscoverPayload::essence,
                ClientboundDiscoverPayload::create);
    }
}
