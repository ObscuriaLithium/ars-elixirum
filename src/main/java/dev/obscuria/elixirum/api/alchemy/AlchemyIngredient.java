package dev.obscuria.elixirum.api.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy._AlchemyIngredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface AlchemyIngredient extends ItemLike {

    static Codec<AlchemyIngredient> codec() {
        return _AlchemyIngredient.CODEC;
    }

    static AlchemyIngredient of(Item item) {
        return new _AlchemyIngredient(item);
    }

    static AlchemyIngredient of(ItemStack stack) {
        return new _AlchemyIngredient(stack.getItem());
    }

    Item asItem();

    ItemStack asStack();

    default AlchemyProperties properties(Alchemy alchemy) {
        return alchemy.ingredients().propertiesOf(asItem());
    }
}
