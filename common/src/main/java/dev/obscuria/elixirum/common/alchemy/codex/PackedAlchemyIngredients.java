package dev.obscuria.elixirum.common.alchemy.codex;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.item.Item;

import java.util.Map;

public record PackedAlchemyIngredients(
        Map<Item, AlchemyProperties> properties
) {

    public static final Codec<PackedAlchemyIngredients> CODEC;
    public static final PackedAlchemyIngredients EMPTY;

    static {
        CODEC = Codec
                .unboundedMap(CodecHelper.STRICT_ITEM, AlchemyProperties.CODEC)
                .xmap(PackedAlchemyIngredients::new, PackedAlchemyIngredients::properties);
        EMPTY = new PackedAlchemyIngredients(Map.of());
    }
}
