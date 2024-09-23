package dev.obscuria.elixirum.datagen;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

final class DefaultPrefixes {
    private static final List<Entry> ENTRIES = Lists.newArrayList();

    static void acceptTranslations(BiConsumer<String, String> consumer) {
        ENTRIES.forEach(entry -> consumer.accept(entry.languageKey, entry.languageValue));
    }

    static void acceptPrefixes(TriConsumer<ResourceLocation, Optional<Holder<MobEffect>>, String> consumer) {
        ENTRIES.forEach(entry -> consumer.accept(entry.id, entry.source, entry.languageKey));
    }

    private static void register(String id, Holder<MobEffect> source, String name) {
        ENTRIES.add(new Entry(Elixirum.key(id), Optional.of(source), "effect_prefix.elixirum." + id, name));
    }

    record Entry(ResourceLocation id,
                 Optional<Holder<MobEffect>> source,
                 String languageKey,
                 String languageValue) {
    }

    static {
        register("speed_quick", MobEffects.MOVEMENT_SPEED, "Quick");
        register("speed_fleet_footed", MobEffects.MOVEMENT_SPEED, "Fleet-Footed");
        register("speed_zooming", MobEffects.MOVEMENT_SPEED, "Zooming");
        register("speed_swift", MobEffects.MOVEMENT_SPEED, "Swift");
        register("speed_zippy", MobEffects.MOVEMENT_SPEED, "Zippy");

        register("slowness_sluggish", MobEffects.MOVEMENT_SLOWDOWN, "Sluggish");
        register("slowness_lethargic", MobEffects.MOVEMENT_SLOWDOWN, "Lethargic");
        register("slowness_plodding", MobEffects.MOVEMENT_SLOWDOWN, "Plodding");
        register("slowness_dawdling", MobEffects.MOVEMENT_SLOWDOWN, "Dawdling");
        register("slowness_soggy", MobEffects.MOVEMENT_SLOWDOWN, "Soggy");

        register("haste_frenzied", MobEffects.DIG_SPEED, "Frenzied");
        register("haste_frantic", MobEffects.DIG_SPEED, "Frantic");
        register("haste_turbocharged", MobEffects.DIG_SPEED, "Turbocharged");
        register("haste_rapid_fire", MobEffects.DIG_SPEED, "Rapid-Fire");
        register("haste_zealous", MobEffects.DIG_SPEED, "Zealous");

        register("mining_fatigue_heavy_handed", MobEffects.DIG_SLOWDOWN, "Heavy-Handed");
        register("mining_fatigue_cumbersome", MobEffects.DIG_SLOWDOWN, "Cumbersome");
        register("mining_fatigue_dilly_dallying", MobEffects.DIG_SLOWDOWN, "Dilly-Dallying");
        register("mining_fatigue_bureaucratic", MobEffects.DIG_SLOWDOWN, "Bureaucratic");
        register("mining_fatigue_dreary", MobEffects.DIG_SLOWDOWN, "Dreary");

        register("strength_devastating", MobEffects.DAMAGE_BOOST, "Devastating");
        register("strength_punishing", MobEffects.DAMAGE_BOOST, "Punishing");
        register("strength_raging", MobEffects.DAMAGE_BOOST, "Raging");
        register("strength_cataclysmic", MobEffects.DAMAGE_BOOST, "Cataclysmic");
        register("strength_annihilating", MobEffects.DAMAGE_BOOST, "Annihilating");

        register("instant_health_revitalizing", MobEffects.HEAL, "Revitalizing");
        register("instant_health_nurturing", MobEffects.HEAL, "Nurturing");
        register("instant_health_soothing", MobEffects.HEAL, "Soothing");
        register("instant_health_rejuvenating", MobEffects.HEAL, "Rejuvenating");
        register("instant_health_restorative", MobEffects.HEAL, "Restorative");

        register("instant_damage_mortal", MobEffects.HARM, "Mortal");
        register("instant_damage_doomed", MobEffects.HARM, "Doomed");
        register("instant_damage_lethal", MobEffects.HARM, "Lethal");
        register("instant_damage_grim", MobEffects.HARM, "Grim");
        register("instant_damage_terminal", MobEffects.HARM, "Terminal");

        register("jump_boost_bouncy", MobEffects.JUMP, "Bouncy");
        register("jump_boost_sprightly", MobEffects.JUMP, "Sprightly");
        register("jump_boost_aerodynamic", MobEffects.JUMP, "Aerodynamic");
        register("jump_boost_frolicking", MobEffects.JUMP, "Frolicking");
        register("jump_boost_vaulting", MobEffects.JUMP, "Vaulting");

        register("nausea_nauseating", MobEffects.CONFUSION, "Nauseating");
        register("nausea_queasy", MobEffects.CONFUSION, "Queasy");
        register("nausea_sickening", MobEffects.CONFUSION, "Sickening");
        register("nausea_giddy", MobEffects.CONFUSION, "Giddy");
        register("nausea_stale", MobEffects.CONFUSION, "Stale");

        register("regeneration_rapid", MobEffects.REGENERATION, "Rapid");
        register("regeneration_restorative", MobEffects.REGENERATION, "Restorative");
        register("regeneration_self_sustaining", MobEffects.REGENERATION, "Self-Sustaining");
        register("regeneration_vibrant", MobEffects.REGENERATION, "Vibrant");
        register("regeneration_invigorating", MobEffects.REGENERATION, "Invigorating");

        register("resistance_resilient", MobEffects.DAMAGE_RESISTANCE, "Resilient");
        register("resistance_unyielding", MobEffects.DAMAGE_RESISTANCE, "Unyielding");
        register("resistance_steadfast", MobEffects.DAMAGE_RESISTANCE, "Steadfast");
        register("resistance_robust", MobEffects.DAMAGE_RESISTANCE, "Robust");
        register("resistance_tenacious", MobEffects.DAMAGE_RESISTANCE, "Tenacious");

        register("fire_resistance_flame_retardant", MobEffects.FIRE_RESISTANCE, "Flame-Retardant");
        register("fire_resistance_fireproof", MobEffects.FIRE_RESISTANCE, "Fireproof");
        register("fire_resistance_heat_resistant", MobEffects.FIRE_RESISTANCE, "Heat-Resistant");
        register("fire_resistance_blaze_defying", MobEffects.FIRE_RESISTANCE, "Blaze-Defying");
        register("fire_resistance_cindersafe", MobEffects.FIRE_RESISTANCE, "Cindersafe");

        register("water_breathing_aquatic", MobEffects.WATER_BREATHING, "Aquatic");
        register("water_breathing_submarine", MobEffects.WATER_BREATHING, "Submarine");
        register("water_breathing_hydro_activated", MobEffects.WATER_BREATHING, "Hydro-Activated");
        register("water_breathing_gills_enabled", MobEffects.WATER_BREATHING, "Gills-Enabled");
        register("water_breathing_liquid_breathing", MobEffects.WATER_BREATHING, "Liquid-Breathing");

        register("invisibility_unseen", MobEffects.INVISIBILITY, "Unseen");
        register("invisibility_invisible", MobEffects.INVISIBILITY, "Invisible");
        register("invisibility_shadowy", MobEffects.INVISIBILITY, "Shadowy");
        register("invisibility_ghostly", MobEffects.INVISIBILITY, "Ghostly");
        register("invisibility_veiled", MobEffects.INVISIBILITY, "Veiled");

        register("blindness_myopic", MobEffects.BLINDNESS, "Myopic");
        register("blindness_sightless", MobEffects.BLINDNESS, "Sightless");
        register("blindness_obscured", MobEffects.BLINDNESS, "Obscured");
        register("blindness_hazy", MobEffects.BLINDNESS, "Hazy");
        register("blindness_squinted", MobEffects.BLINDNESS, "Squinted");

        register("night_vision_nocturnal", MobEffects.NIGHT_VISION, "Nocturnal");
        register("night_vision_shadow_seeing", MobEffects.NIGHT_VISION, "Shadow-Seeing");
        register("night_vision_dark_adapted", MobEffects.NIGHT_VISION, "Dark-Adapted");
        register("night_vision_twilight_sensitive", MobEffects.NIGHT_VISION, "Twilight-Sensitive");
        register("night_vision_gloom_seeker", MobEffects.NIGHT_VISION, "Gloom-Seeker");

        register("hunger_insatiable", MobEffects.HUNGER, "Insatiable");
        register("hunger_gluttonous", MobEffects.HUNGER, "Gluttonous");
        register("hunger_voracious", MobEffects.HUNGER, "Voracious");
        register("hunger_devouring", MobEffects.HUNGER, "Devouring");
        register("hunger_large_mouth", MobEffects.HUNGER, "Large-Mouth");

        register("weakness_feeble", MobEffects.WEAKNESS, "Feeble");
        register("weakness_fragile", MobEffects.WEAKNESS, "Fragile");
        register("weakness_diminished", MobEffects.WEAKNESS, "Diminished");
        register("weakness_stunted", MobEffects.WEAKNESS, "Stunted");
        register("weakness_waning", MobEffects.WEAKNESS, "Waning");

        register("poison_smelly", MobEffects.POISON, "Smelly");
        register("poison_venomous", MobEffects.POISON, "Venomous");
        register("poison_toxic", MobEffects.POISON, "Toxic");
        register("poison_foul", MobEffects.POISON, "Foul");
        register("poison_nasty", MobEffects.POISON, "Nasty");

        register("wither_desiccated", MobEffects.WITHER, "Desiccated");
        register("wither_parched", MobEffects.WITHER, "Parched");
        register("wither_withered", MobEffects.WITHER, "Withered");
        register("wither_drought_stricken", MobEffects.WITHER, "Drought-Stricken");
        register("wither_shriveled", MobEffects.WITHER, "Shriveled");

        register("health_boost_revitalizing", MobEffects.HEALTH_BOOST, "Revitalizing");
        register("health_boost_invigorating", MobEffects.HEALTH_BOOST, "Invigorating");
        register("health_boost_rejuvenating", MobEffects.HEALTH_BOOST, "Rejuvenating");
        register("health_boost_fortifying", MobEffects.HEALTH_BOOST, "Fortifying");
        register("health_boost_energizing", MobEffects.HEALTH_BOOST, "Energizing");

        register("absorption_suctioning", MobEffects.ABSORPTION, "Suctioning");
        register("absorption_ingesting", MobEffects.ABSORPTION, "Ingesting");
        register("absorption_swallowing", MobEffects.ABSORPTION, "Swallowing");
        register("absorption_devouring", MobEffects.ABSORPTION, "Devouring");
        register("absorption_assimilating", MobEffects.ABSORPTION, "Assimilating");

        register("saturation_stuffed", MobEffects.SATURATION, "Stuffed");
        register("saturation_overindulgent", MobEffects.SATURATION, "Overindulgent");
        register("saturation_sated", MobEffects.SATURATION, "Sated");
        register("saturation_gorged", MobEffects.SATURATION, "Gorged");
        register("saturation_full_bellied", MobEffects.SATURATION, "Full-Bellied");

        register("glowing_radiant", MobEffects.GLOWING, "Radiant");
        register("glowing_glistening", MobEffects.GLOWING, "Glistening");
        register("glowing_luminous", MobEffects.GLOWING, "Luminous");
        register("glowing_brilliant", MobEffects.GLOWING, "Brilliant");
        register("glowing_fluorescent", MobEffects.GLOWING, "Fluorescent");

        register("levitation_floating", MobEffects.LEVITATION, "Floating");
        register("levitation_weightless", MobEffects.LEVITATION, "Weightless");
        register("levitation_suspended", MobEffects.LEVITATION, "Suspended");
        register("levitation_ethereal", MobEffects.LEVITATION, "Ethereal");
        register("levitation_buoyant", MobEffects.LEVITATION, "Buoyant");

        register("luck_fortunate", MobEffects.LUCK, "Fortunate");
        register("luck_serendipitous", MobEffects.LUCK, "Serendipitous");
        register("luck_lucky", MobEffects.LUCK, "Lucky");
        register("luck_favorable", MobEffects.LUCK, "Favorable");
        register("luck_full_of_fate", MobEffects.LUCK, "Full of Fate");

        register("unluck_unfortunate", MobEffects.UNLUCK, "Unfortunate");
        register("unluck_accursed", MobEffects.UNLUCK, "Accursed");
        register("unluck_unlucky", MobEffects.UNLUCK, "Unlucky");
        register("unluck_fateful", MobEffects.UNLUCK, "Fateful");
        register("unluck_mishap_prone", MobEffects.UNLUCK, "Mishap-Prone");

        register("slow_falling_fluffy", MobEffects.SLOW_FALLING, "Fluffy");
        register("slow_falling_gossamer", MobEffects.SLOW_FALLING, "Gossamer");
        register("slow_falling_airy", MobEffects.SLOW_FALLING, "Airy");
        register("slow_falling_bubbly", MobEffects.SLOW_FALLING, "Bubbly");
        register("slow_falling_cottony", MobEffects.SLOW_FALLING, "Cottony");
    }
}
