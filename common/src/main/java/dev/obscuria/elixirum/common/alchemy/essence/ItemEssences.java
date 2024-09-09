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

public abstract class ItemEssences {
    protected final HashMap<Item, ItemEssenceHolder> holders = Maps.newHashMap();

    public boolean hasHolder(Item item) {
        return this.holders.containsKey(item);
    }

    public ItemEssenceHolder getHolder(Item item) {
        return this.holders.getOrDefault(item, ItemEssenceHolder.EMPTY);
    }

    public void setHolder(Item item, ItemEssenceHolder holder) {
        this.holders.put(item, holder);
        this.whenExternallyModified();
    }

    public void removeHolder(Item item) {
        this.holders.remove(item);
        this.whenExternallyModified();
    }

    public void removeAllHolders() {
        this.holders.clear();
        this.whenExternallyModified();
    }

    public Packed pack() {
        return new Packed(Maps.newHashMap(this.holders));
    }

    public void unpack(Packed packed) {
        this.holders.clear();
        this.holders.putAll(packed.holders);
    }

    protected abstract void whenExternallyModified();

    public record Packed(Map<Item, ItemEssenceHolder> holders) {
        public static final Codec<Packed> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, Packed> STREAM_CODEC;

        static {
            CODEC = Codec
                    .unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), ItemEssenceHolder.CODEC)
                    .xmap(Packed::new, Packed::holders);
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.registry(Registries.ITEM),
                            ItemEssenceHolder.STREAM_CODEC), Packed::holders,
                    Packed::new);
        }
    }
}
