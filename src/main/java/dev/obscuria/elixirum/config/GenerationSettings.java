package dev.obscuria.elixirum.config;

import dev.obscuria.elixirum.config.properties.ConfigProperty;
import dev.obscuria.elixirum.config.properties.IdMapperProperty;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import static dev.obscuria.elixirum.config.properties.ConfigProperty.direct;
import static dev.obscuria.elixirum.config.properties.ConfigProperty.idMapped;

@UtilityClass
public final class GenerationSettings {

    public final ConfigProperty<Boolean> ESSENCE_BLACKLIST_ENABLED = direct(GenerationConfig.ESSENCE_BLACKLIST_ENABLED);
    public final ConfigProperty<Boolean> ESSENCE_WHITELIST_ENABLED = direct(GenerationConfig.ESSENCE_WHITELIST_ENABLED);
    public final IdMapperProperty<MobEffect> ESSENCE_BLACKLIST = idMapped(BuiltInRegistries.MOB_EFFECT, GenerationConfig.ESSENCE_BLACKLIST, GenerationConfig.INVALIDATED);
    public final IdMapperProperty<MobEffect> ESSENCE_WHITELIST = idMapped(BuiltInRegistries.MOB_EFFECT, GenerationConfig.ESSENCE_WHITELIST, GenerationConfig.INVALIDATED);

    public final ConfigProperty<Boolean> INGREDIENT_BLACKLIST_ENABLED = direct(GenerationConfig.INGREDIENT_BLACKLIST_ENABLED);
    public final ConfigProperty<Boolean> INGREDIENT_WHITELIST_ENABLED = direct(GenerationConfig.INGREDIENT_WHITELIST_ENABLED);
    public final IdMapperProperty<Item> INGREDIENT_BLACKLIST = idMapped(BuiltInRegistries.ITEM, GenerationConfig.INGREDIENT_BLACKLIST, GenerationConfig.INVALIDATED);
    public final IdMapperProperty<Item> INGREDIENT_WHITELIST = idMapped(BuiltInRegistries.ITEM, GenerationConfig.INGREDIENT_WHITELIST, GenerationConfig.INVALIDATED);
}