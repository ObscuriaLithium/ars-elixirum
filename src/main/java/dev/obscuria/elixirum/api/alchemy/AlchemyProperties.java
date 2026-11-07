package dev.obscuria.elixirum.api.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy._AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface AlchemyProperties extends EssenceProvider {

    static Codec<AlchemyProperties> codec() {
        return _AlchemyProperties.CODEC;
    }

    static AlchemyProperties empty() {
        return _AlchemyProperties.EMPTY;
    }

    static AlchemyProperties create(
            Aspect aspect, EssenceHolderMap essences,
            Focus focus, Form form, Risk risk
    ) {
        return new _AlchemyProperties(aspect, essences, focus, form, risk);
    }

    Aspect aspect();

    EssenceHolderMap essences();

    Focus focus();

    Form form();

    Risk risk();

    default boolean isEmpty() {
        return essences().isEmpty();
    }

    default void forEachEssence(BiConsumer<EssenceHolder, Integer> consumer) {
        essences().forEachSorted(consumer);
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
