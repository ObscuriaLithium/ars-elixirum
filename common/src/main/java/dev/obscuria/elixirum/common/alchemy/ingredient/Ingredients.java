package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Ingredients implements Iterable<Ingredients.Entry> {
    protected final HashMap<Item, IngredientProperties> properties = Maps.newHashMap();
    private int totalEssences;

    public boolean hasProperties(Item item) {
        return this.properties.containsKey(item);
    }

    public IngredientProperties getProperties(Item item) {
        return this.properties.getOrDefault(item, IngredientProperties.EMPTY);
    }

    public void setProperties(Item item, IngredientProperties properties) {
        this.properties.put(item, properties);
        this.whenExternallyModified();
    }

    public void removeProperties(Item item) {
        this.properties.remove(item);
        this.whenExternallyModified();
    }

    public void removeAllProperties() {
        this.properties.clear();
        this.whenExternallyModified();
    }

    public int getTotalEssences() {
        return totalEssences;
    }

    public int getTotalIngredients() {
        return properties.size();
    }

    public Packed pack() {
        return new Packed(Maps.newHashMap(properties));
    }

    public void unpack(Packed packed) {
        this.properties.clear();
        this.properties.putAll(packed.properties);
        this.computeTotalEssences();
    }

    @Override
    public Iterator<Entry> iterator() {
        return properties.entrySet().stream()
                .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                .iterator();
    }

    protected abstract void whenExternallyModified();

    protected void computeTotalEssences() {
        this.totalEssences = this.properties.values().stream()
                .mapToInt(properties -> properties.getEssences().size())
                .sum();
    }

    public record Packed(Map<Item, IngredientProperties> properties) {
        public static final Codec<Packed> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, Packed> STREAM_CODEC;

        static {
            CODEC = Codec
                    .unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), IngredientProperties.CODEC)
                    .xmap(Packed::new, Packed::properties);
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new,
                            ByteBufCodecs.registry(Registries.ITEM),
                            IngredientProperties.STREAM_CODEC),
                    Packed::properties,
                    Packed::new);
        }
    }

    public record Entry(Item item, IngredientProperties properties) {}
}
