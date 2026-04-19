package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import dev.obscuria.fragmentum.util.color.RGB;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class EssenceHolderMap implements EssenceProvider {

    public static final EssenceHolderMap EMPTY;
    public static final Codec<EssenceHolderMap> CODEC;

    private final Object2IntMap<EssenceHolder> holderToWeight;
    private final List<Object2IntMap.Entry<EssenceHolder>> sorted;
    private boolean initialized = false;

    public static EssenceHolderMap single(EssenceHolder holder, int count) {
        return new EssenceHolderMap(Map.of(holder, count));
    }

    public EssenceHolderMap(Map<EssenceHolder, Integer> holders) {
        this.holderToWeight = new Object2IntOpenHashMap<>(holders);
        this.sorted = new ArrayList<>();
    }

    public boolean isEmpty() {
        return holderToWeight.isEmpty();
    }

    public int size() {
        return holderToWeight.size();
    }

    public int get(EssenceHolder essence) {
        return holderToWeight.getOrDefault(essence, 0);
    }

    public EssenceHolder dominant() {
        if (holderToWeight.isEmpty()) throw new NullPointerException("Essence map is empty.");
        return sorted().get(0).getKey();
    }

    public RGB dominantColor() {
        return holderToWeight.isEmpty()
                ? ElixirContents.WATER.color()
                : dominant().map(Essence::color).orElseGet(ElixirContents.WATER::color);
    }

    public boolean contains(EssenceHolder essence) {
        return holderToWeight.containsKey(essence);
    }

    public void forEach(BiConsumer<EssenceHolder, Integer> consumer) {
        this.holderToWeight.forEach(consumer);
    }

    public @Unmodifiable List<Object2IntMap.Entry<EssenceHolder>> sorted() {
        if (!initialized) {
            this.sorted.clear();
            this.sorted.addAll(makeSortedList());
            this.initialized = true;
        }
        return sorted;
    }

    @Override
    public Stream<Object2IntMap.Entry<EssenceHolder>> streamSorted() {
        return sorted().stream();
    }

    @Override
    public void forEachSorted(BiConsumer<EssenceHolder, Integer> consumer) {
        for (var entry : sorted()) {
            consumer.accept(entry.getKey(), entry.getIntValue());
        }
    }

    private Map<EssenceHolder, Integer> source() {
        return holderToWeight;
    }

    private List<Object2IntMap.Entry<EssenceHolder>> makeSortedList() {
        return holderToWeight.object2IntEntrySet().stream()
                .sorted(Comparator.comparingInt(Object2IntMap.Entry<EssenceHolder>::getIntValue).reversed())
                .toList();
    }

    static {
        EMPTY = new EssenceHolderMap(Map.of());
        CODEC = Codec.unboundedMap(EssenceHolder.CODEC, Codec.INT).xmap(EssenceHolderMap::new, EssenceHolderMap::source);
    }
}
