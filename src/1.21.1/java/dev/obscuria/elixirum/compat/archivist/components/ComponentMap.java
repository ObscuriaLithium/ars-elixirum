package dev.obscuria.elixirum.compat.archivist.components;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ComponentMap {

    private final Map<ComponentKey<?>, Object> values = new IdentityHashMap<>();

    private ComponentMap() {}

    public static ComponentMap empty() {
        return new ComponentMap();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(ComponentKey<? extends T> key, Supplier<T> supplier) {
        var value = (T) values.get(key);
        if (value != null) return value;
        value = supplier.get();
        values.put(key, value);
        return value;
    }

    public void clear() {
        values.clear();
    }

    public void putAll(Map<ComponentKey<?>, Object> values) {
        this.values.putAll(values);
    }

    public Map<ComponentKey<?>, Object> asMap() {
        return new IdentityHashMap<>(values);
    }
}
