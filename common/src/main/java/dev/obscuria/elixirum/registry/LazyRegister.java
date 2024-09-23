package dev.obscuria.elixirum.registry;

import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public final class LazyRegister<TSource> {
    private final Map<ResourceLocation, Entry<TSource>> values = Maps.newHashMap();
    private final ResourceKey<? extends Registry<TSource>> registryKey;
    private final Registry<TSource> registry;
    private final String namespace;

    public static <TSource> LazyRegister<TSource>
    create(Registry<TSource> registry, String namespace) {
        return new LazyRegister<>(registry, namespace);
    }

    public <TValue extends TSource> LazyValue<TSource, TValue>
    register(final String name, Supplier<TValue> supplier) {
        final var id = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        var value = LazyValue.<TSource, TValue>create(this.registryKey, id);
        this.values.put(id, new Entry<>(value, supplier));
        return value;
    }

    public void register(RegisterFunction<TSource> function) {
        this.values.forEach((id, entry) -> function.register(this.registry, id, entry));
    }

    public void register() {
        this.values.forEach((id, entry) -> entry.value.bind(
                Registry.registerForHolder(this.registry, id, entry.factory.get())));
    }

    public ResourceKey<? extends Registry<TSource>> getRegistryKey() {
        return this.registryKey;
    }

    private LazyRegister(Registry<TSource> registry, String namespace) {
        this.registryKey = registry.key();
        this.registry = registry;
        this.namespace = namespace;
    }

    @FunctionalInterface
    public interface RegisterFunction<TSource> {

        Holder.Reference<TSource> register(Registry<TSource> registry,
                                           ResourceLocation id,
                                           Supplier<? extends TSource> factory);

        private void register(Registry<TSource> registry, ResourceLocation id, Entry<TSource> entry) {
            entry.value.bind(this.register(registry, id, entry.factory));
        }
    }

    private record Entry<TSource>(LazyValue<TSource, ?> value, Supplier<? extends TSource> factory) { }
}
