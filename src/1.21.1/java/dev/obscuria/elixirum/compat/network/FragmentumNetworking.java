package dev.obscuria.elixirum.compat.network;

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
    private static final Map<Class<?>, CustomPacketPayload.Type<?>> TYPES = new HashMap<>();

    public static Registrar registrar(String namespace) {
        return new Registrar(namespace, dev.obscuria.fragmentum.content.network.FragmentumNetworking.registrar(namespace));
    }

    public static <T> void sendTo(ServerPlayer player, T payload) {
        dev.obscuria.fragmentum.content.network.FragmentumNetworking.sendTo(player, wrap(payload));
    }

    public static <T> void sendToServer(T payload) {
        dev.obscuria.fragmentum.content.network.FragmentumNetworking.sendToServer(wrap(payload));
    }

    @SuppressWarnings("unchecked")
    private static <T> Wrapped<T> wrap(T payload) {
        var type = (CustomPacketPayload.Type<Wrapped<T>>) TYPES.get(payload.getClass());
        if (type == null) throw new IllegalArgumentException("Unregistered payload " + payload.getClass().getName());
        return new Wrapped<>(type, payload);
    }

    private record Wrapped<T>(CustomPacketPayload.Type<Wrapped<T>> type, T value) implements CustomPacketPayload {}

    public record Registrar(String namespace, dev.obscuria.fragmentum.content.network.PayloadRegistrar delegate) {
        public <T> void registerClientbound(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder, BiConsumer<Player, T> handler) {
            var type = registerType(clazz);
            delegate.registerClientbound(wrapperClass(), type, codec(type, encoder, decoder), (player, wrapped) -> handler.accept(player, wrapped.value()));
        }

        public <T> void registerServerbound(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder, BiConsumer<ServerPlayer, T> handler) {
            var type = registerType(clazz);
            delegate.registerServerbound(wrapperClass(), type, codec(type, encoder, decoder), (player, wrapped) -> handler.accept(player, wrapped.value()));
        }

        private <T> CustomPacketPayload.Type<Wrapped<T>> registerType(Class<T> clazz) {
            var type = new CustomPacketPayload.Type<Wrapped<T>>(ResourceLocation.fromNamespaceAndPath(namespace, clazz.getSimpleName().toLowerCase(Locale.ROOT)));
            if (TYPES.putIfAbsent(clazz, type) != null) throw new IllegalStateException("Duplicate payload " + clazz.getName());
            return type;
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
