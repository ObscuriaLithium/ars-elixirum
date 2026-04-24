package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record AlchemyProperties(
        Aspect aspect,
        EssenceHolderMap essences,
        Form application,
        Risk risk,
        Focus focus
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
                Form.CODEC.fieldOf("form").forGetter(AlchemyProperties::application),
                Risk.CODEC.fieldOf("risk").forGetter(AlchemyProperties::risk),
                Focus.CODEC.fieldOf("focus").forGetter(AlchemyProperties::focus)
        ).apply(codec, AlchemyProperties::new));
        EMPTY = new AlchemyProperties(
                Aspect.NONE,
                EssenceHolderMap.EMPTY,
                Form.POTABLE,
                Risk.BALANCED,
                Focus.BALANCED);
    }
}
