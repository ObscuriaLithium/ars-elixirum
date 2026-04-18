package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.client.ClientPayloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public record ClientboundAlchemyPayload(
        @Nullable CompoundTag essencesTag,
        @Nullable CompoundTag ingredientsTag,
        @Nullable CompoundTag profileTag
) {

    public static void encode(ClientboundAlchemyPayload payload, FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(payload.essencesTag());
        byteBuf.writeNbt(payload.ingredientsTag());
        byteBuf.writeNbt(payload.profileTag());
    }

    public static ClientboundAlchemyPayload decode(FriendlyByteBuf byteBuf) {
        return new ClientboundAlchemyPayload(
                byteBuf.readAnySizeNbt(),
                byteBuf.readAnySizeNbt(),
                byteBuf.readAnySizeNbt());
    }

    public static void handle(Player player, ClientboundAlchemyPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }
}
