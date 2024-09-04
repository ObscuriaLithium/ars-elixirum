package dev.obscuria.elixirum.common.alchemy.properties;

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

public abstract class AlchemyPropertyMap {
    protected final HashMap<Item, AlchemyProperties> properties = Maps.newHashMap();

    public boolean hasProperties(Item item) {
        return this.properties.containsKey(item);
    }

    public AlchemyProperties getProperties(Item item) {
        return this.properties.getOrDefault(item, AlchemyProperties.EMPTY);
    }

    public void setProperties(Item item, AlchemyProperties properties) {
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

    public Packed pack() {
        return new Packed(Maps.newHashMap(this.properties));
    }

    public void unpack(Packed packed) {
        this.properties.clear();
        this.properties.putAll(packed.properties);
    }

    protected abstract void whenExternallyModified();

    public record Packed(Map<Item, AlchemyProperties> properties) {
        public static final Codec<Packed> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, Packed> STREAM_CODEC;

        static {
            CODEC = Codec
                    .unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), AlchemyProperties.DIRECT_CODEC)
                    .stable().xmap(Packed::new, Packed::properties);
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.registry(Registries.ITEM),
                            AlchemyProperties.STREAM_CODEC), Packed::properties,
                    Packed::new);
        }
    }
}
