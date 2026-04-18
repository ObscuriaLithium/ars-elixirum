package dev.obscuria.elixirum.common.alchemy.ingredient.properties;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.brewing.AspectShift;

import java.util.List;

public record CatalystProperties(
        List<AspectShift> aspectShifts
) {

    public static final Codec<CatalystProperties> CODEC;
    public static final CatalystProperties EMPTY;

    static {
        EMPTY = new CatalystProperties(List.of());
        CODEC = Codec.list(AspectShift.CODEC).xmap(CatalystProperties::new, CatalystProperties::aspectShifts);
    }
}
