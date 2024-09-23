package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.affix.Affix;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record IngredientPreset(Item target,
                               Map<ResourceLocation, Integer> essences,
                               List<Affix> affixes) {
    public static final Codec<IngredientPreset> DIRECT_CODEC;

    public static IngredientPreset single(Item item, ResourceLocation essence, int weight) {
        return new IngredientPreset(item, Util.make(Maps.newHashMap(), map -> map.put(essence, weight)), List.of());
    }

    public IngredientProperties build(RegistryAccess access) {
        final var registry = access.registry(ElixirumRegistries.ESSENCE).orElseThrow();
        return IngredientProperties.create(this.essences.entrySet().stream()
                        .filter(entry -> registry.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                affixes);
    }

    static {
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("target").forGetter(IngredientPreset::target),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("essences").forGetter(IngredientPreset::essences),
                Affix.CODEC.listOf().optionalFieldOf("affixes", List.of()).forGetter(IngredientPreset::affixes)
        ).apply(instance, IngredientPreset::new));
    }
}
