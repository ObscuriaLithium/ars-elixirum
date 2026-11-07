package dev.obscuria.elixirum.compat.archivist.components;

import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.Objects;

public final class ComponentKey<T> {

    private final Codec<T> codec;

    private ComponentKey(Codec<T> codec) {
        this.codec = Objects.requireNonNull(codec);
    }

    public static <T> ComponentKey<T> create(Codec<T> codec) {
        return new ComponentKey<>(codec);
    }

    public static Codec<Map<ComponentKey<?>, Object>> createValueMapCodec(Codec<ComponentKey<?>> keyCodec) {
        return Codec.dispatchedMap(keyCodec, key -> key.codec);
    }
}
