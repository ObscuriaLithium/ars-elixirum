package dev.obscuria.elixirum.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class LazyHolder<TSource, TValue extends TSource> implements Holder<TSource> {

    public static <R, T extends R> LazyHolder<R, T>
    create(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
        return create(ResourceKey.create(registryKey, valueName));
    }

    public static <R, T extends R> LazyHolder<R, T>
    create(ResourceLocation registryName, ResourceLocation valueName) {
        return create(ResourceKey.createRegistryKey(registryName), valueName);
    }

    public static <R, T extends R> LazyHolder<R, T>
    create(ResourceKey<R> key) {
        return new LazyHolder<>(key);
    }

    private final ResourceKey<TSource> key;
    private @Nullable Holder<TSource> holder;

    private LazyHolder(ResourceKey<TSource> key) {
        this.key = Objects.requireNonNull(key);
        this.bind(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TValue value() {
        bind(true);
        if (this.holder == null) {
            throw new NullPointerException("Trying to access unbound value: " + this.key);
        }

        return (TValue) this.holder.value();
    }

    public Holder<TSource> sourceHolder() {
        bind(true);
        if (this.holder == null) {
            throw new NullPointerException("Trying to access unbound value: " + this.key);
        }
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Holder<?> h && h.kind() == Kind.REFERENCE && h.unwrapKey().orElseThrow() == this.key;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DeferredHolder{%s}", this.key);
    }

    @Override
    public boolean isBound() {
        bind(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation id) {
        return id.equals(this.key.location());
    }

    @Override
    public boolean is(ResourceKey<TSource> key) {
        return key == this.key;
    }

    @Override
    public boolean is(Predicate<ResourceKey<TSource>> filter) {
        return filter.test(this.key);
    }

    @Override
    public boolean is(TagKey<TSource> tag) {
        bind(false);
        return this.holder != null && this.holder.is(tag);
    }

    @Override
    @Deprecated
    public boolean is(Holder<TSource> holder) {
        bind(false);
        return this.holder != null && this.holder.is(holder);
    }

    @Override
    public Stream<TagKey<TSource>> tags() {
        bind(false);
        return this.holder != null ? this.holder.tags() : Stream.empty();
    }

    @Override
    public Either<ResourceKey<TSource>, TSource> unwrap() {
        return Either.left(this.key);
    }

    @Override
    public Optional<ResourceKey<TSource>> unwrapKey() {
        return Optional.of(this.key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<TSource> owner) {
        bind(false);
        return this.holder != null && this.holder.canSerializeIn(owner);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Registry<TSource> getRegistry() {
        return (Registry<TSource>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    private void bind(boolean throwIfMissing) {
        if (this.holder != null) return;

        Registry<TSource> registry = getRegistry();
        if (registry != null) {
            this.holder = registry.getHolder(this.key).orElse(null);
        } else if (throwIfMissing) {
            throw new IllegalStateException("Registry not present for " + this + ": " + this.key.registry());
        }
    }
}
