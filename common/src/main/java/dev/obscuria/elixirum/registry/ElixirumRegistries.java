package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ConfiguredElixir;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirPrefix;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientPreset;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface ElixirumRegistries {
    ResourceKey<Registry<Essence>> ESSENCE = ResourceKey.createRegistryKey(Elixirum.key("essence"));
    ResourceKey<Registry<IngredientPreset>> INGREDIENT_PRESET = ResourceKey.createRegistryKey(Elixirum.key("ingredient_preset"));
    ResourceKey<Registry<ElixirPrefix>> ELIXIR_PREFIX = ResourceKey.createRegistryKey(Elixirum.key("elixir_prefix"));
    ResourceKey<Registry<ConfiguredElixir>> CONFIGURED_ELIXIR = ResourceKey.createRegistryKey(Elixirum.key("configured_elixir"));
}
