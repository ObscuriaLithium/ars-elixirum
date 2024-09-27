package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.network.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

public enum FabricPayload {
    CLIENTBOUND_INGREDIENTS(Side.CLIENTBOUND, "s2c_ingredients",
            ClientboundItemEssencesPacket.class,
            ClientboundItemEssencesPacket.STREAM_CODEC,
            ClientNetworkHandler::handle),
    CLIENTBOUND_PROFILE(Side.CLIENTBOUND, "s2c_profile",
            ClientboundProfilePacket.class,
            ClientboundProfilePacket.STREAM_CODEC,
            ClientNetworkHandler::handle),
    CLIENTBOUND_DISCOVER(Side.CLIENTBOUND, "s2c_discover",
            ClientboundDiscoverPacket.class,
            ClientboundDiscoverPacket.STREAM_CODEC,
            ClientNetworkHandler::handle),
    SERVERBOUND_PROFILE(Side.SERVERBOUND, "c2s_profile",
            ServerboundProfilePacket.class,
            ServerboundProfilePacket.STREAM_CODEC,
            ServerNetworkHandler::handle),
    SERVERBOUND_COLLECTION_ACTION(Side.SERVERBOUND, "c2s_collection_action",
            ServerboundCollectionActionPacket.class,
            ServerboundCollectionActionPacket.STREAM_CODEC,
            ServerNetworkHandler::handle);

    private final Description<?> description;
    private final Side side;

    public static Optional<CustomPacketPayload> wrap(Object object) {
        for (var payload : values()) {
            final var result = payload.description.wrap(payload, object);
            if (result.isEmpty()) continue;
            return result;
        }
        return Optional.empty();
    }

    <T> FabricPayload(Side side,
                      String name,
                      Class<T> valueClass,
                      StreamCodec<RegistryFriendlyByteBuf, T> valueCodec,
                      Handler<T> handler) {
        this.side = side;
        this.description = new Description<>(
                valueClass,
                new CustomPacketPayload.Type<>(Elixirum.key(name)),
                StreamCodec.composite(
                        valueCodec, Instance::value,
                        value -> new Instance<>(this, value)),
                handler);
    }

    private record Instance<T>(FabricPayload payload, T value) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return this.payload.description.type();
        }
    }

    private record Description<T>(
            Class<T> valueClass,
            CustomPacketPayload.Type<Instance<T>> type,
            StreamCodec<RegistryFriendlyByteBuf, Instance<T>> streamCodec,
            Handler<T> handler) {

        private void registerS2C() {
            PayloadTypeRegistry.playS2C().register(type, streamCodec);
        }

        private void registerC2S() {
            PayloadTypeRegistry.playC2S().register(type, streamCodec);
        }

        private void registerClientReceiver() {
            ClientPlayNetworking.registerGlobalReceiver(type,
                    (payload, context) -> handler.handle(payload.value(), context.player()));
        }

        private void registerServerReceiver() {
            ServerPlayNetworking.registerGlobalReceiver(type,
                    (payload, context) -> handler.handle(payload.value(), context.player()));
        }

        private Optional<CustomPacketPayload> wrap(FabricPayload payload, Object object) {
            return valueClass.isInstance(object)
                    ? Optional.of(new Instance<>(payload, valueClass.cast(object)))
                    : Optional.empty();
        }
    }

    @FunctionalInterface
    private interface Handler<T> {

        void handle(T value, Player source);
    }

    static void registerTypes() {
        for (var packet : values())
            switch (packet.side) {
                case CLIENTBOUND -> packet.description.registerS2C();
                case SERVERBOUND -> packet.description.registerC2S();
                default -> throw new IllegalStateException();
            }
    }

    static void registerClientReceivers() {
        Arrays.stream(values())
                .filter(payload -> payload.side == Side.CLIENTBOUND)
                .forEach(payload -> payload.description.registerClientReceiver());
    }

    static void registerServerReceivers() {
        Arrays.stream(values())
                .filter(payload -> payload.side == Side.SERVERBOUND)
                .forEach(payload -> payload.description.registerServerReceiver());
    }

    private enum Side {
        CLIENTBOUND,
        SERVERBOUND
    }
}
