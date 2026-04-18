package dev.obscuria.elixirum.common.alchemy.ingredient.properties;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Temper;

public record FoundationProperties(
        Temper temper
) {

    public static final Codec<FoundationProperties> CODEC;
    public static final FoundationProperties BALANCED;

    static {
        BALANCED = new FoundationProperties(Temper.BALANCED);
        CODEC = Temper.CODEC.xmap(FoundationProperties::new, FoundationProperties::temper);
    }
}
