package dev.obscuria.elixirum.compat.network;

import dev.obscuria.fragmentum.api.network.PacketPayload;
import dev.obscuria.fragmentum.api.network.PayloadRegistrar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FragmentumNetworking {
    private static final Map<Class<?>, PacketPayload.Info<?>> PAYLOADS = new HashMap<>();

    public static Registrar registrar(String namespace) {
        return new Registrar(namespace, dev.obscuria.fragmentum.api.network.FragmentumNetworking.INSTANCE.registrar(namespace));
    }

    public static <T> void sendTo(ServerPlayer player, T payload) {
        dev.obscuria.fragmentum.api.network.FragmentumNetworking.INSTANCE.sendTo(player, wrap(payload));
    }

    public static <T> void sendToServer(T payload) {
        dev.obscuria.fragmentum.api.network.FragmentumNetworking.INSTANCE.sendToServer(wrap(payload));
    }

    @SuppressWarnings("unchecked")
    private static <T> Wrapped<T> wrap(T payload) {
        var info = PAYLOADS.get(payload.getClass());
        if (info == null) throw new IllegalArgumentException("Unregistered payload " + payload.getClass().getName());
        return new Wrapped<>((CustomPacketPayload.Type<Wrapped<T>>) info.getPayloadType(), payload);
    }

    private record Wrapped<T>(CustomPacketPayload.Type<Wrapped<T>> type, T value) implements PacketPayload {
        @Override public PacketPayload.Info<?> info() { return PAYLOADS.get(value.getClass()); }
    }

    public record Registrar(String namespace, PayloadRegistrar delegate) {
        public <T> void registerClientbound(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder, BiConsumer<Player, T> handler) {
            delegate.registerClientbound(register(clazz, encoder, decoder, handler));
        }

        public <T> void registerServerbound(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder, BiConsumer<ServerPlayer, T> handler) {
            delegate.registerServerbound(register(clazz, encoder, decoder, (player, value) -> handler.accept((ServerPlayer) player, value)));
        }

        private <T> PacketPayload.Info<Wrapped<T>> register(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
                Function<FriendlyByteBuf, T> decoder, BiConsumer<Player, T> handler) {
            var type = new CustomPacketPayload.Type<Wrapped<T>>(ResourceLocation.fromNamespaceAndPath(namespace, clazz.getSimpleName().toLowerCase(Locale.ROOT)));
            var info = new PacketPayload.Info<>(Registrar.<T>wrapperClass(), type, codec(type, encoder, decoder),
                    (player, wrapped) -> handler.accept(player, wrapped.value()));
            if (PAYLOADS.putIfAbsent(clazz, info) != null) throw new IllegalStateException("Duplicate payload " + clazz.getName());
            return info;
        }

        private static <T> StreamCodec<RegistryFriendlyByteBuf, Wrapped<T>> codec(CustomPacketPayload.Type<Wrapped<T>> type,
                BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder) {
            return StreamCodec.of((buffer, wrapped) -> encoder.accept(wrapped.value(), buffer), buffer -> new Wrapped<>(type, decoder.apply(buffer)));
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static <T> Class<Wrapped<T>> wrapperClass() {
            return (Class) Wrapped.class;
        }
    }
}
