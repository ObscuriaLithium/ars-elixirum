package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirData;
import dev.obscuria.elixirum.common.alchemy.ElixirStyle;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public interface ElixirumDataComponents {
    LazyRegister<DataComponentType<?>> REGISTER = LazyRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Elixirum.MODID);

    LazyHolder<DataComponentType<?>, DataComponentType<ElixirStyle>> ELIXIR_STYLE =
            simple("elixir_style", builder -> builder
                    .persistent(ElixirStyle.CODEC)
                    .networkSynchronized(ElixirStyle.STREAM_CODEC)
                    .cacheEncoding());
    LazyHolder<DataComponentType<?>, DataComponentType<ElixirData>> ELIXIR_DATA =
            simple("elixir_data", builder -> builder
                    .persistent(ElixirData.CODEC)
                    .networkSynchronized(ElixirData.STREAM_CODEC)
                    .cacheEncoding());

    private static <TValue> LazyHolder<DataComponentType<?>, DataComponentType<TValue>>
    simple(final String name, UnaryOperator<DataComponentType.Builder<TValue>> builder) {
        return REGISTER.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
