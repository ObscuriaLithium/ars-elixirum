package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.CatalystProperties;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.FoundationProperties;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.StabilizerProperties;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record AlchemyProperties(
        Aspect aspect,
        EssenceHolderMap essences,
        FoundationProperties foundation,
        CatalystProperties catalyst,
        StabilizerProperties stabilizer
) implements EssenceProvider {

    public static final Codec<AlchemyProperties> CODEC;
    public static final AlchemyProperties EMPTY;

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
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.fieldOf("aspect").forGetter(AlchemyProperties::aspect),
                EssenceHolderMap.CODEC.fieldOf("essences").forGetter(AlchemyProperties::essences),
                FoundationProperties.CODEC.fieldOf("foundation").forGetter(AlchemyProperties::foundation),
                CatalystProperties.CODEC.fieldOf("catalyst").forGetter(AlchemyProperties::catalyst),
                StabilizerProperties.CODEC.fieldOf("stabilizer").forGetter(AlchemyProperties::stabilizer)
        ).apply(codec, AlchemyProperties::new));
        EMPTY = new AlchemyProperties(
                Aspect.NONE,
                EssenceHolderMap.EMPTY,
                FoundationProperties.BALANCED,
                CatalystProperties.EMPTY,
                StabilizerProperties.EMPTY);
    }
}
