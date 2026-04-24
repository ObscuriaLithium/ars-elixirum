package dev.obscuria.elixirum.common.alchemy.codex;

import dev.obscuria.elixirum.api.codex.AlchemyIngredients;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractAlchemyIngredients implements AlchemyIngredients {

    protected final Map<Item, AlchemyProperties> propertiesMap = new HashMap<>();

    public PackedAlchemyIngredients pack() {
        return new PackedAlchemyIngredients(propertiesMap);
    }

    public boolean contains(Item item) {
        return propertiesMap.containsKey(item);
    }

    @Override
    public AlchemyProperties propertiesOf(Item item) {
        return propertiesMap.getOrDefault(item, AlchemyProperties.EMPTY);
    }

    @Override
    public AlchemyProperties propertiesOf(ItemStack stack) {
        return propertiesOf(stack.getItem());
    }

    @Override
    public void forEach(BiConsumer<Item, AlchemyProperties> consumer) {
        this.propertiesMap.forEach(consumer);
    }

    protected void unpack(PackedAlchemyIngredients packed) {
        this.propertiesMap.clear();
        this.propertiesMap.putAll(packed.properties());
    }

    protected void register(Item item, AlchemyProperties properties) {
        this.propertiesMap.put(item, properties);
    }
}
