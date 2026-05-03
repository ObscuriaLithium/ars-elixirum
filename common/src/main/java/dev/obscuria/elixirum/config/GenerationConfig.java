package dev.obscuria.elixirum.config;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.fragmentum.config.ConfigBuilder;
import dev.obscuria.fragmentum.config.ConfigValue;
import dev.obscuria.fragmentum.util.signal.Signal0;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class GenerationConfig {

    final List<ConfigValue<?>> VALUES = new ArrayList<>();

    final ConfigValue<Boolean> ESSENCE_BLACKLIST_ENABLED;
    final ConfigValue<Boolean> ESSENCE_WHITELIST_ENABLED;
    final ConfigValue<List<? extends String>> ESSENCE_BLACKLIST;
    final ConfigValue<List<? extends String>> ESSENCE_WHITELIST;

    final ConfigValue<Boolean> INGREDIENT_BLACKLIST_ENABLED;
    final ConfigValue<Boolean> INGREDIENT_WHITELIST_ENABLED;
    final ConfigValue<List<? extends String>> INGREDIENT_BLACKLIST;
    final ConfigValue<List<? extends String>> INGREDIENT_WHITELIST;

    final Signal0 INVALIDATED = new Signal0();

    public static void refresh() {
        INVALIDATED.emit();
    }

    static {
        final var builder = new ConfigBuilder("obscuria/ars_elixirum-generation.toml");

        builder.push("Essence Generation");
        ESSENCE_BLACKLIST_ENABLED = register(builder.define("essenceBlacklistEnabled", true));
        ESSENCE_WHITELIST_ENABLED = register(builder.define("essenceWhitelistEnabled", false));
        ESSENCE_BLACKLIST = register(builder.defineList("essenceBlacklist", new ArrayList<>()));
        ESSENCE_WHITELIST = register(builder.defineList("essenceWhitelist", new ArrayList<>()));
        builder.pop();

        builder.push("Ingredient Generation");
        INGREDIENT_BLACKLIST_ENABLED = register(builder.define("ingredientBlacklistEnabled", true));
        INGREDIENT_WHITELIST_ENABLED = register(builder.define("ingredientWhitelistEnabled", false));
        INGREDIENT_BLACKLIST = register(builder.defineList("ingredientBlacklist", new ArrayList<>()));
        INGREDIENT_WHITELIST = register(builder.defineList("ingredientWhitelist", new ArrayList<>()));
        builder.pop();

        builder.buildCommon(ArsElixirum.MODID);
    }

    private static <T> ConfigValue<T> register(ConfigValue<T> value) {
        VALUES.add(value);
        return value;
    }

    public static void init() {}
}