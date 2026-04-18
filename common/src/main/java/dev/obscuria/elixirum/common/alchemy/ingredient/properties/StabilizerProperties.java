package dev.obscuria.elixirum.common.alchemy.ingredient.properties;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;

import java.util.List;

public record StabilizerProperties(
        List<Aspect> purgeTargets
) {

    public static final Codec<StabilizerProperties> CODEC;
    public static final StabilizerProperties EMPTY;

    static {
        EMPTY = new StabilizerProperties(List.of());
        CODEC = Codec.list(Aspect.CODEC).xmap(StabilizerProperties::new, StabilizerProperties::purgeTargets);
    }
}
