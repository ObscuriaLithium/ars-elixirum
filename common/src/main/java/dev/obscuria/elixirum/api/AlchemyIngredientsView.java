package dev.obscuria.elixirum.api;

import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface AlchemyIngredientsView {

    int totalIngredients();

    int totalEssences();

    AlchemyProperties propertiesOf(Item item);

    AlchemyProperties propertiesOf(ItemStack stack);

    Map<Item, AlchemyProperties> asMapView();
}
