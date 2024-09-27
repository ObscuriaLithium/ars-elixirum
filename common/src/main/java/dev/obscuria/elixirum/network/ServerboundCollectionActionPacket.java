package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ServerboundCollectionActionPacket(ElixirHolder holder, Action action) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCollectionActionPacket> STREAM_CODEC;

    public static ServerboundCollectionActionPacket create(ElixirHolder holder, Action action) {
        return new ServerboundCollectionActionPacket(holder, action);
    }

    public static ServerboundCollectionActionPacket add(ElixirHolder holder) {
        return new ServerboundCollectionActionPacket(holder, Action.ADD);
    }

    public static ServerboundCollectionActionPacket update(ElixirHolder holder) {
        return new ServerboundCollectionActionPacket(holder, Action.UPDATE);
    }

    public static ServerboundCollectionActionPacket remove(ElixirHolder holder) {
        return new ServerboundCollectionActionPacket(holder, Action.REMOVE);
    }

    static {
        final var actionCodec = StreamCodec.<RegistryFriendlyByteBuf, Action>ofMember(
                (action, buf) -> buf.writeEnum(action),
                buf -> buf.readEnum(Action.class));
        STREAM_CODEC = StreamCodec.composite(
                ElixirHolder.STREAM_CODEC, ServerboundCollectionActionPacket::holder,
                actionCodec, ServerboundCollectionActionPacket::action,
                ServerboundCollectionActionPacket::create);
    }

    public enum Action {
        ADD,
        UPDATE,
        REMOVE
    }
}
