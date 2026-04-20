package dev.obscuria.elixirum.common.network;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.ClientPayloadListener;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record ClientboundDiscoverEssencePayload(Item item, EssenceHolder essence) {

    public static final PayloadCodec<ClientboundDiscoverEssencePayload> PAYLOAD_CODEC;

    public static void encode(ClientboundDiscoverEssencePayload payload, FriendlyByteBuf byteBuf) {
        PAYLOAD_CODEC.write(byteBuf, payload);
    }

    public static ClientboundDiscoverEssencePayload decode(FriendlyByteBuf byteBuf) {
        return PAYLOAD_CODEC.read(byteBuf);
    }

    public static void handle(Player player, ClientboundDiscoverEssencePayload payload) {
        ClientPayloadListener.handle(player, payload);
    }

    static {
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(RecordCodecBuilder.create(codec -> codec.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ClientboundDiscoverEssencePayload::item),
                EssenceHolder.CODEC.fieldOf("essence").forGetter(ClientboundDiscoverEssencePayload::essence)
        ).apply(codec, ClientboundDiscoverEssencePayload::new)), FragmentumProxy::registryAccess);
    }
}
