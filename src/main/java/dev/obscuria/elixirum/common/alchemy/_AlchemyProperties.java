package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;

public record _AlchemyProperties(
        Aspect aspect,
        EssenceHolderMap essences,
        Focus focus,
        Form form,
        Risk risk
) implements AlchemyProperties {

    public static final Codec<AlchemyProperties> CODEC;
    public static final AlchemyProperties EMPTY;

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.fieldOf("aspect").forGetter(AlchemyProperties::aspect),
                EssenceHolderMap.CODEC.fieldOf("essences").forGetter(AlchemyProperties::essences),
                Focus.CODEC.fieldOf("focus").forGetter(AlchemyProperties::focus),
                Form.CODEC.fieldOf("form").forGetter(AlchemyProperties::form),
                Risk.CODEC.fieldOf("risk").forGetter(AlchemyProperties::risk)
        ).apply(codec, _AlchemyProperties::new));
        EMPTY = new _AlchemyProperties(
                Aspect.NONE,
                EssenceHolderMap.EMPTY,
                Focus.BALANCED,
                Form.POTABLE,
                Risk.BALANCED);
    }
}
