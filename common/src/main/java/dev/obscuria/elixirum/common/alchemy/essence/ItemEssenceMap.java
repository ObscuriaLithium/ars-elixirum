package dev.obscuria.elixirum.common.alchemy.essence;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public abstract class ItemEssenceMap {
    protected final HashMap<Item, ItemEssences> map = Maps.newHashMap();

    public boolean hasProperties(Item item) {
        return this.map.containsKey(item);
    }

    public ItemEssences getProperties(Item item) {
        return this.map.getOrDefault(item, ItemEssences.EMPTY);
    }

    public void setProperties(Item item, ItemEssences properties) {
        this.map.put(item, properties);
        this.whenExternallyModified();
    }

    public void removeProperties(Item item) {
        this.map.remove(item);
        this.whenExternallyModified();
    }

    public void removeAllProperties() {
        this.map.clear();
        this.whenExternallyModified();
    }

    public Packed pack() {
        return new Packed(Maps.newHashMap(this.map));
    }

    public void unpack(Packed packed) {
        this.map.clear();
        this.map.putAll(packed.properties);
    }

    protected abstract void whenExternallyModified();

    public record Packed(Map<Item, ItemEssences> properties) {
        public static final Codec<Packed> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, Packed> STREAM_CODEC;

        static {
            CODEC = Codec
                    .unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), ItemEssences.DIRECT_CODEC)
                    .stable().xmap(Packed::new, Packed::properties);
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.registry(Registries.ITEM),
                            ItemEssences.STREAM_CODEC), Packed::properties,
                    Packed::new);
        }
    }
}
