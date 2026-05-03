package dev.obscuria.elixirum.server.alchemy.generation.operations;

import org.jetbrains.annotations.Nullable;

public interface Operation<T> {

    @Nullable
    default T value() {
        return null;
    }
}
