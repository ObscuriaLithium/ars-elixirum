package dev.obscuria.elixirum.common.alchemy.style;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record StyleVariant(int index, Shape shape, Cap cap) {

    public static Stream<StyleVariant> allVariants() {
        final var index = new AtomicInteger(1);
        return Arrays.stream(Shape.values())
                .flatMap(shape -> Arrays.stream(Cap.values())
                        .map(cap -> new StyleVariant(index.getAndIncrement(), shape, cap)));
    }
}