package dev.obscuria.elixirum.registry;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.Map;
import java.util.function.Supplier;

public final class LazyRegister<TSource> {
    private final Map<ResourceLocation, Supplier<? extends TSource>> values = Maps.newHashMap();
    private final ResourceKey<? extends Registry<TSource>> registryKey;
    private final Registry<TSource> registry;
    private final String namespace;

    public static <TSource> LazyRegister<TSource>
    create(Registry<TSource> registry, String namespace) {
        return new LazyRegister<>(registry, namespace);
    }

    public <TValue extends TSource> LazyValue<TSource, TValue>
    register(final String name, Supplier<TValue> supplier) {
        final var location = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        this.values.put(location, supplier);
        return LazyValue.create(this.registryKey, location);
    }

    @SuppressWarnings("unchecked")
    public void register(TriConsumer<Registry<TSource>, ResourceLocation, Supplier<TSource>> consumer) {
        this.values.forEach((name, supplier) -> consumer.accept(this.registry, name, (Supplier<TSource>) supplier));
    }

    public ResourceKey<? extends Registry<TSource>> getRegistryKey() {
        return this.registryKey;
    }

    private LazyRegister(Registry<TSource> registry, String namespace) {
        this.registryKey = registry.key();
        this.registry = registry;
        this.namespace = namespace;
    }
}
