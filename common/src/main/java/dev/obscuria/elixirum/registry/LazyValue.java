package dev.obscuria.elixirum.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class LazyValue<TSource, TValue extends TSource> {

    public static <R, T extends R> LazyValue<R, T>
    create(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
        return create(ResourceKey.create(registryKey, valueName));
    }

    public static <R, T extends R> LazyValue<R, T>
    create(ResourceLocation registryName, ResourceLocation valueName) {
        return create(ResourceKey.createRegistryKey(registryName), valueName);
    }

    public static <R, T extends R> LazyValue<R, T>
    create(ResourceKey<R> key) {
        return new LazyValue<>(key);
    }

    private final ResourceKey<TSource> key;
    private @Nullable Holder.Reference<TSource> holder;

    private LazyValue(ResourceKey<TSource> key) {
        this.key = Objects.requireNonNull(key);
    }

    @SuppressWarnings("unchecked")
    public TValue value() {
        if (this.holder == null) throw new NullPointerException("Trying to access unbound value: " + this.key);
        return (TValue) this.holder.value();
    }

    public Holder<TSource> holder() {
        if (this.holder == null) throw new NullPointerException("Trying to access unbound value: " + this.key);
        return this.holder;
    }

    public Optional<TValue> asOptional() {
        return isBound() ? Optional.of(value()) : Optional.empty();
    }

    public ResourceLocation getId() {
        return this.key.location();
    }

    public ResourceKey<TSource> getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DeferredHolder{%s}", this.key);
    }

    public boolean isBound() {
        return this.holder != null && this.holder.isBound();
    }

    public boolean is(ResourceLocation id) {
        return id.equals(this.key.location());
    }

    public boolean is(ResourceKey<TSource> key) {
        return key == this.key;
    }

    public boolean is(Predicate<ResourceKey<TSource>> filter) {
        return filter.test(this.key);
    }

    public boolean is(TagKey<TSource> tag) {
        return this.holder != null && this.holder.is(tag);
    }

    public Stream<TagKey<TSource>> tags() {
        return this.holder != null ? this.holder.tags() : Stream.empty();
    }

    void bind(Holder.Reference<TSource> holder) {
        if (this.holder != null) return;
        this.holder = holder;
    }
}
