package dev.obscuria.elixirum.common.alchemy.elixir;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssenceHolder;
import dev.obscuria.elixirum.server.ServerAlchemy;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class ElixirMixer {
    public static final Codec<ElixirMixer> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirMixer> STREAM_CODEC;
    private final Map<Holder<Essence>, Data> essenceMap = Maps.newHashMap();

    public ElixirMixer() {}

    private ElixirMixer(Map<Holder<Essence>, Data> initialMap) {
        this.essenceMap.putAll(initialMap);
    }

    public boolean append(HolderLookup.Provider lookup, ItemStack stack) {
        return append(lookup, stack.getItem());
    }

    public boolean append(HolderLookup.Provider lookup, Item item) {
        var essences = ServerAlchemy.getItemEssences().getHolder(item);
        if (essences.isEmpty()) return false;
        appendIngredient(lookup, item, essences);
        return true;
    }

    public void append(HolderLookup.Provider lookup, ElixirRecipe recipe) {
        for (var item : recipe.ingredients()) append(lookup, item);
    }

    public ElixirContents brew() {
        final var builder = ElixirContents.create();
        for (var entry : essenceMap.entrySet()) {
            final var essence = entry.getKey();
            final var data = entry.getValue();
            builder.addEffect(ElixirEffect.byWeight(essence, data.weight, data.weight));
        }
        builder.computeContentColor();
        return builder.build();
    }

    private void appendIngredient(HolderLookup.Provider lookup, Item item, ItemEssenceHolder essences) {
        for (var entry : essences.getEssences(lookup).object2IntEntrySet()) {
            final var test = entry.getKey().value().getName();
            essenceMap.computeIfPresent(entry.getKey(), (holder, data) -> data.append(item, entry.getIntValue()));
            essenceMap.computeIfAbsent(entry.getKey(), holder -> new Data(item, entry.getIntValue()));
        }
    }

    private Map<Holder<Essence>, Data> getEssenceMap() {
        return this.essenceMap;
    }

    private static final class Data {
        private static final Codec<Data> CODEC;
        private static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC;
        private final Object2IntMap<Item> ingredients = new Object2IntOpenHashMap<>();
        private double weight;

        private Data(Item item, double weight) {
            this.ingredients.put(item, 1);
            this.weight = weight;
        }

        private Data(Map<Item, Integer> initialMap, double weight) {
            this.ingredients.putAll(initialMap);
            this.weight = weight;
        }

        private Data append(Item item, double weight) {
            if (ingredients.containsKey(item)) {
                this.ingredients.computeInt(item, (key, value) -> value + 1);
                this.weight += weight;
            } else {
                this.ingredients.put(item, 1);
                this.weight += weight;
            }
            return this;
        }

        private Map<Item, Integer> getIngredients() {
            return this.ingredients;
        }

        private double getWeight() {
            return this.weight;
        }

        static {
            CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT).fieldOf("ingredients").forGetter(Data::getIngredients),
                    Codec.DOUBLE.fieldOf("weight").forGetter(Data::getWeight)
            ).apply(instance, Data::new));
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.INT), Data::getIngredients,
                    ByteBufCodecs.DOUBLE, Data::getWeight,
                    Data::new);
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Essence.CODEC, Data.CODEC).fieldOf("holders").forGetter(ElixirMixer::getEssenceMap)
        ).apply(instance, ElixirMixer::new));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, Essence.STREAM_CODEC, Data.STREAM_CODEC), ElixirMixer::getEssenceMap,
                ElixirMixer::new);
    }
}
