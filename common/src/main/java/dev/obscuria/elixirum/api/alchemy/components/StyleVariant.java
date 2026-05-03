package dev.obscuria.elixirum.api.alchemy.components;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.components._StyleVariant;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import dev.obscuria.fragmentum.network.PayloadCodec;

public interface StyleVariant {

    static StyleVariant of(Cap cap, Shape shape) {
        return new _StyleVariant(cap, shape);
    }

    static StyleVariant defaultVariant() {
        return _StyleVariant.DEFAULT;
    }

    static StyleVariant elixirVariant() {
        return _StyleVariant.ELIXIR;
    }

    static StyleVariant splashElixirVariant() {
        return _StyleVariant.SPLASH_ELIXIR;
    }

    static StyleVariant lingeringElixirVariant() {
        return _StyleVariant.LINGERING_ELIXIR;
    }

    static Codec<StyleVariant> codec() {
        return _StyleVariant.CODEC;
    }

    static Codec<StyleVariant> packedCodec() {
        return _StyleVariant.PACKED_CODEC;
    }

    static PayloadCodec<StyleVariant> payloadCodec() {
        return _StyleVariant.PAYLOAD_CODEC;
    }

    static StyleVariant unpack(int packed) {
        return of(
                Cap.byId(packed & 0xFF),
                Shape.byId((packed >> 8) & 0xFF));
    }

    Cap cap();

    Shape shape();

    default boolean isDefault() {
        return cap() == Cap.DEFAULT && shape() == Shape.DEFAULT;
    }

    default StyleVariant withCap(Cap cap) {
        return of(cap, shape());
    }

    default StyleVariant withShape(Shape shape) {
        return of(cap(), shape);
    }

    default int pack() {
        return (cap().id & 0xFF) | ((shape().id & 0xFF) << 8);
    }
}