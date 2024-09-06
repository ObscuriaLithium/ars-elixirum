package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssencePreset;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface ElixirumRegistries {
    ResourceKey<Registry<Essence>> ESSENCE = ResourceKey.createRegistryKey(Elixirum.key("essence"));
    ResourceKey<Registry<ItemEssencePreset>> ESSENCE_PRESET = ResourceKey.createRegistryKey(Elixirum.key("essence_preset"));
}
