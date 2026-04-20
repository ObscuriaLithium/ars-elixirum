package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.api.AlchemyIngredientsView;
import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class ClientAlchemyIngredients implements AlchemyIngredientsView {

    private AlchemyIngredientsData data = AlchemyIngredientsData.empty();

    ClientAlchemyIngredients() {}

    public void update(AlchemyIngredientsData data) {
        this.data = data;
    }

    @Override
    public int totalIngredients() {
        return data.totalIngredients();
    }

    @Override
    public int totalEssences() {
        return data.totalEssences();
    }

    @Override
    public AlchemyProperties propertiesOf(Item item) {
        return data.propertiesOf(item);
    }

    @Override
    public AlchemyProperties propertiesOf(ItemStack stack) {
        return data.propertiesOf(stack);
    }

    @Override
    public Map<Item, AlchemyProperties> asMapView() {
        return data.asMapView();
    }
}
