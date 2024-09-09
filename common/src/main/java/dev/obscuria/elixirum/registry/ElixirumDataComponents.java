package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public interface ElixirumDataComponents {
    LazyRegister<DataComponentType<?>> SOURCE = LazyRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Elixirum.MODID);

    LazyValue<DataComponentType<?>, DataComponentType<ElixirStyle>> ELIXIR_STYLE =
            simple("elixir_style", builder -> builder
                    .persistent(ElixirStyle.CODEC)
                    .networkSynchronized(ElixirStyle.STREAM_CODEC)
                    .cacheEncoding());
    LazyValue<DataComponentType<?>, DataComponentType<ElixirContents>> ELIXIR_CONTENTS =
            simple("elixir_contents", builder -> builder
                    .persistent(ElixirContents.CODEC)
                    .networkSynchronized(ElixirContents.STREAM_CODEC)
                    .cacheEncoding());

    LazyValue<DataComponentType<?>, DataComponentType<ExtractContents>> EXTRACT_CONTENTS =
            simple("extract_contents", builder -> builder
                    .persistent(ExtractContents.CODEC)
                    .networkSynchronized(ExtractContents.STREAM_CODEC)
                    .cacheEncoding());

    private static <TValue> LazyValue<DataComponentType<?>, DataComponentType<TValue>>
    simple(final String name, UnaryOperator<DataComponentType.Builder<TValue>> builder) {
        return SOURCE.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
