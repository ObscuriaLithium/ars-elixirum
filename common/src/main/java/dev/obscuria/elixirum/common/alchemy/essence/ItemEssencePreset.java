package dev.obscuria.elixirum.common.alchemy.essence;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.stream.Collectors;

public record ItemEssencePreset(Item target, Map<ResourceLocation, Integer> essences) {
    public static final Codec<ItemEssencePreset> DIRECT_CODEC;

    public static ItemEssencePreset single(Item item, ResourceLocation essence, int weight) {
        return new ItemEssencePreset(item, Util.make(Maps.newHashMap(), map -> map.put(essence, weight)));
    }

    public ItemEssences build(RegistryAccess access) {
        final var registry = access.registry(ElixirumRegistries.ESSENCE).orElseThrow();
        return ItemEssences.create(this.essences.entrySet().stream()
                .filter(entry -> registry.containsKey(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> registry.getHolder(entry.getKey()).orElseThrow(),
                        Map.Entry::getValue)));
    }

    static {

        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("target").forGetter(ItemEssencePreset::target),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).stable().fieldOf("essences").forGetter(ItemEssencePreset::essences)
        ).apply(instance, ItemEssencePreset::new));
    }
}
