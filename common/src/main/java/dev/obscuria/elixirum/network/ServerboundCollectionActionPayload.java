package dev.obscuria.elixirum.network;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundCollectionActionPayload(ElixirHolder holder, Action action) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCollectionActionPayload> STREAM_CODEC;
    public static final CustomPacketPayload.Type<ServerboundCollectionActionPayload> TYPE;

    public static ServerboundCollectionActionPayload create(ElixirHolder holder, Action action)
    {
        return new ServerboundCollectionActionPayload(holder, action);
    }

    public static ServerboundCollectionActionPayload add(ElixirHolder holder)
    {
        return new ServerboundCollectionActionPayload(holder, Action.ADD);
    }

    public static ServerboundCollectionActionPayload update(ElixirHolder holder)
    {
        return new ServerboundCollectionActionPayload(holder, Action.UPDATE);
    }

    public static ServerboundCollectionActionPayload remove(ElixirHolder holder)
    {
        return new ServerboundCollectionActionPayload(holder, Action.REMOVE);
    }

    public static void handle(ServerPlayer player, ServerboundCollectionActionPayload payload)
    {
        ServerNetworkHandler.handle(player, payload);
    }

    @Override
    public Type<ServerboundCollectionActionPayload> type()
    {
        return TYPE;
    }

    static
    {
        TYPE = new Type<>(Elixirum.key("serverbound_collection_action"));
        final var actionCodec = StreamCodec.<RegistryFriendlyByteBuf, Action>ofMember(
                (action, buf) -> buf.writeEnum(action),
                buf -> buf.readEnum(Action.class));
        STREAM_CODEC = StreamCodec.composite(
                ElixirHolder.STREAM_CODEC, ServerboundCollectionActionPayload::holder,
                actionCodec, ServerboundCollectionActionPayload::action,
                ServerboundCollectionActionPayload::create);
    }

    public enum Action
    {
        ADD,
        UPDATE,
        REMOVE
    }
}
