package dev.obscuria.elixirum.api.alchemy;

import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface EssenceProvider {

    Stream<Object2IntMap.Entry<EssenceHolder>> streamSorted();

    void forEachSorted(BiConsumer<EssenceHolder, Integer> consumer);
}
