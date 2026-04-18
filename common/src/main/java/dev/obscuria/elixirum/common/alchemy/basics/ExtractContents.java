package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record ExtractContents(
        EssenceHolderMap essences
) implements EssenceProvider {
    public static final ExtractContents EMPTY;
    public static final Codec<ExtractContents> CODEC;

    public boolean isEmpty() {
        return essences.isEmpty();
    }

    @Override
    public Stream<Object2IntMap.Entry<EssenceHolder>> streamSorted() {
        return essences.streamSorted();
    }

    @Override
    public void forEachSorted(BiConsumer<EssenceHolder, Integer> consumer) {
        this.essences.forEachSorted(consumer);
    }

    static {
        EMPTY = new ExtractContents(EssenceHolderMap.EMPTY);
        CODEC = EssenceHolderMap.CODEC.xmap(ExtractContents::new, ExtractContents::essences);
    }
}
