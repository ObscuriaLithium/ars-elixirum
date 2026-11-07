package dev.obscuria.elixirum.api.alchemy.components;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.alchemy.EssenceProvider;
import dev.obscuria.elixirum.common.alchemy.components._ExtractContents;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface ExtractContents extends EssenceProvider {

    static Codec<ExtractContents> codec() {
        return _ExtractContents.CODEC;
    }

    static ExtractContents empty() {
        return _ExtractContents.EMPTY;
    }

    static ExtractContents create(Item source, EssenceHolderMap essences) {
        return new _ExtractContents(source, essences);
    }

    Item source();

    EssenceHolderMap essences();

    default boolean isEmpty() {
        return essences().isEmpty();
    }

    @Override
    default Stream<Object2IntMap.Entry<EssenceHolder>> streamSorted() {
        return essences().streamSorted();
    }

    @Override
    default void forEachSorted(BiConsumer<EssenceHolder, Integer> consumer) {
        this.essences().forEachSorted(consumer);
    }
}
