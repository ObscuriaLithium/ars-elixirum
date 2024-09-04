package dev.obscuria.elixirum.common.alchemy.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public record PropertyDefinition(Item target, AlchemyProperties properties) {
    public static final Codec<PropertyDefinition> DIRECT_CODEC;

    static {
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("target").forGetter(PropertyDefinition::target),
                AlchemyProperties.DIRECT_CODEC.fieldOf("properties").forGetter(PropertyDefinition::properties)
        ).apply(instance, PropertyDefinition::new));
    }
}
