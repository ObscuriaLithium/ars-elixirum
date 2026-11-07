package dev.obscuria.elixirum.config.properties;

import dev.obscuria.fragmentum.config.ConfigValue;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class IdMapperProperty<T> implements ConfigProperty<List<T>> {

    private final Registry<T> registry;
    private final ConfigValue<List<? extends String>> configValue;
    private List<T> bakedValue = List.of();

    IdMapperProperty(Registry<T> registry, ConfigValue<List<? extends String>> configValue, Signal0 signal) {
        this.registry = registry;
        this.configValue = configValue;
        signal.connect(this::rebake);
    }

    public boolean has(T value) {
        return bakedValue.contains(value);
    }

    @Override
    public List<T> get() {
        return bakedValue;
    }

    private void rebake() {
        this.bakedValue = configValue.get().stream()
                .map(ResourceLocation::new)
                .map(registry::get)
                .toList();
    }
}
