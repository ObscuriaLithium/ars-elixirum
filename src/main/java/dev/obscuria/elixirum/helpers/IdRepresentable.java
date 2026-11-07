package dev.obscuria.elixirum.helpers;

import com.mojang.serialization.Codec;

public interface IdRepresentable {

    static <T extends Enum<T> & IdRepresentable> Codec<T> fromEnum(T[] values, T defaultValue) {
        return Codec.INT.xmap(id -> {
            for (var value : values) {
                if (value.getId() != id) continue;
                return value;
            }
            return defaultValue;
        }, IdRepresentable::getId);
    }

    int getId();
}
