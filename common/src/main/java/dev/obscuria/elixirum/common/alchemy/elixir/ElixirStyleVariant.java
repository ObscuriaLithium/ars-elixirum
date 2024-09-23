package dev.obscuria.elixirum.common.alchemy.elixir;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record ElixirStyleVariant(int index, ElixirShape shape, ElixirCap cap) {

    public static Stream<ElixirStyleVariant> allVariants() {
        final var index = new AtomicInteger(1);
        return Arrays.stream(ElixirShape.values())
                .flatMap(shape -> Arrays.stream(ElixirCap.values())
                        .map(cap -> new ElixirStyleVariant(index.getAndIncrement(), shape, cap)));
    }
}