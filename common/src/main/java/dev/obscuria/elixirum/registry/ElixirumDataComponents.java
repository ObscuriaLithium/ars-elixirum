package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public interface ElixirumDataComponents {
    DataComponentType<ElixirStyle> ELIXIR_STYLE =
            register("elixir_style", builder -> builder
                    .persistent(ElixirStyle.CODEC)
                    .networkSynchronized(ElixirStyle.STREAM_CODEC)
                    .cacheEncoding());
    DataComponentType<ElixirContents> ELIXIR_CONTENTS =
            register("elixir_contents", builder -> builder
                    .persistent(ElixirContents.CODEC)
                    .networkSynchronized(ElixirContents.STREAM_CODEC)
                    .cacheEncoding());

    DataComponentType<ExtractContents> EXTRACT_CONTENTS =
            register("extract_contents", builder -> builder
                    .persistent(ExtractContents.CODEC)
                    .networkSynchronized(ExtractContents.STREAM_CODEC)
                    .cacheEncoding());

    private static <T> DataComponentType<T>
    register(final String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        final var component = builder.apply(DataComponentType.builder()).build();
        Elixirum.PLATFORM.registerReference(
                BuiltInRegistries.DATA_COMPONENT_TYPE, Elixirum.key(name),
                () -> component);
        return component;
    }

    static void setup() {}
}
