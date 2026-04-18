package dev.obscuria.elixirum.common.alchemy.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;

public record AspectShift(Aspect aspect, double modifier) {

    public static final Codec<AspectShift> CODEC;

    public double apply(EssenceHolder essence, double weight) {
        return weight * (1.0 + modifier);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.fieldOf("aspect").forGetter(AspectShift::aspect),
                Codec.DOUBLE.fieldOf("modifier").forGetter(AspectShift::modifier)
        ).apply(codec, AspectShift::new));
    }
}
