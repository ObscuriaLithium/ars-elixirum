package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.resources.ResourceKey;

public interface ElixirumEssences {
    ResourceKey<Essence> ABSORPTION = key("absorption");
    ResourceKey<Essence> BLINDNESS = key("blindness");
    ResourceKey<Essence> FIRE_RESISTANCE = key("fire_resistance");
    ResourceKey<Essence> GLOWING = key("glowing");
    ResourceKey<Essence> HASTE = key("haste");
    ResourceKey<Essence> HEALTH_BOOST = key("health_boost");
    ResourceKey<Essence> HUNGER = key("hunger");
    ResourceKey<Essence> INSTANT_DAMAGE = key("instant_damage");
    ResourceKey<Essence> INSTANT_HEALTH = key("instant_health");
    ResourceKey<Essence> INVISIBILITY = key("invisibility");
    ResourceKey<Essence> JUMP_BOOST = key("jump_boost");
    ResourceKey<Essence> LEVITATION = key("levitation");
    ResourceKey<Essence> LUCK = key("luck");
    ResourceKey<Essence> MINING_FATIGUE = key("mining_fatigue");
    ResourceKey<Essence> NAUSEA = key("nausea");
    ResourceKey<Essence> NIGHT_VISION = key("night_vision");
    ResourceKey<Essence> POISON = key("poison");
    ResourceKey<Essence> REGENERATION = key("regeneration");
    ResourceKey<Essence> RESISTANCE = key("resistance");
    ResourceKey<Essence> SATURATION = key("saturation");
    ResourceKey<Essence> SHRINK = key("shrink");
    ResourceKey<Essence> SLOW_FALLING = key("slow_falling");
    ResourceKey<Essence> SLOWNESS = key("slowness");
    ResourceKey<Essence> SPEED = key("speed");
    ResourceKey<Essence> STRENGTH = key("strength");
    ResourceKey<Essence> UNLUCK = key("unluck");
    ResourceKey<Essence> WATER_BREATHING = key("water_breathing");
    ResourceKey<Essence> WEAKNESS = key("weakness");
    ResourceKey<Essence> WITHER = key("wither");

    private static ResourceKey<Essence> key(String name) {
        return ResourceKey.create(ElixirumRegistries.ESSENCE, Elixirum.key(name));
    }
}
