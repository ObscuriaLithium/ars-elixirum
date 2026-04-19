package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolderMap;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public final class AlchemyIngredientsData {

    public static final Codec<AlchemyIngredientsData> CODEC;
    private final Map<Item, AlchemyProperties> source;

    public static AlchemyIngredientsData empty() {
        return new AlchemyIngredientsData(new HashMap<>());
    }

    public static AlchemyIngredientsData copyOf(Map<Item, AlchemyProperties> map) {
        return new AlchemyIngredientsData(new HashMap<>(map));
    }

    private AlchemyIngredientsData(Map<Item, AlchemyProperties> source) {
        this.source = source;
    }

    public int totalIngredients() {
        return source.size();
    }

    public int totalEssences() {
        return source.values().stream()
                .map(AlchemyProperties::essences)
                .mapToInt(EssenceHolderMap::size)
                .sum();
    }

    public AlchemyProperties propertiesOf(Item item) {
        return source.getOrDefault(item, AlchemyProperties.EMPTY);
    }

    public AlchemyProperties propertiesOf(ItemStack stack) {
        return source.getOrDefault(stack.getItem(), AlchemyProperties.EMPTY);
    }

    public boolean contains(Item item) {
        return source.containsKey(item);
    }

    public void clear() {
        source.clear();
    }

    public void putAll(AlchemyIngredientsData other) {
        source.putAll(other.source);
    }

    public void put(Item key, AlchemyProperties essence) {
        source.put(key, essence);
    }

    public void remove(Item key) {
        source.remove(key);
    }

    private static DataResult<Item> findItemByKey(ResourceLocation key) {
        var item = BuiltInRegistries.ITEM.get(key);
        return item != Items.AIR
                ? DataResult.success(item)
                : DataResult.error(() -> "Unknown item '%s'".formatted(key));
    }

    static {
        CODEC = Codec
                .unboundedMap(ResourceLocation.CODEC.comapFlatMap(AlchemyIngredientsData::findItemByKey, BuiltInRegistries.ITEM::getKey), AlchemyProperties.CODEC)
                .xmap(AlchemyIngredientsData::copyOf, it -> it.source);
    }
}
