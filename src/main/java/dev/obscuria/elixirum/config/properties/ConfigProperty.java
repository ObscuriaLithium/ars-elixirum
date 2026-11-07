package dev.obscuria.elixirum.config.properties;

import dev.obscuria.fragmentum.config.ConfigValue;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.core.Registry;

import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface ConfigProperty<T> extends Supplier<T> {

    static <T> ConfigProperty<T> direct(ConfigValue<T> configValue) {
        return configValue::get;
    }

    static <T> IdMapperProperty<T> idMapped(
            Registry<T> registry,
            ConfigValue<List<? extends String>> configValue,
            Signal0 signal
    ) {
        return new IdMapperProperty<>(registry, configValue, signal);
    }
}