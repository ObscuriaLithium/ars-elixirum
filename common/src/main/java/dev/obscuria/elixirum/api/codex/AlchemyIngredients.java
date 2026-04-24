package dev.obscuria.elixirum.api.codex;

import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public interface AlchemyIngredients {

    AlchemyProperties propertiesOf(Item item);

    AlchemyProperties propertiesOf(ItemStack stack);

    void forEach(BiConsumer<Item, AlchemyProperties> consumer);
}
