package dev.obscuria.elixirum.common.alchemy.basics;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class EssenceHolder {

    public static final Codec<EssenceHolder> CODEC;
    private static final Map<ResourceLocation, EssenceHolder> HOLDERS;

    private final ResourceLocation key;
    private @Nullable Essence value;

    public static EssenceHolder getOrCreate(ResourceLocation key) {
        return HOLDERS.computeIfAbsent(key, EssenceHolder::new);
    }

    public static void unbindAll() {
        for (var value : HOLDERS.values()) {
            value.bind(null);
        }
    }

    private EssenceHolder(ResourceLocation key) {
        this.key = key;
    }

    public ResourceLocation key() {
        return key;
    }

    public boolean isBound() {
        return value != null;
    }

    public boolean is(Essence essence) {
        return value == essence;
    }

    public Optional<Essence> get() {
        return Optional.ofNullable(value);
    }

    public <T> Optional<T> map(Function<Essence, T> mapper) {
        return value == null
                ? Optional.empty()
                : Optional.of(mapper.apply(value));
    }

    public Component displayName() {
        return value != null
                ? value.displayName()
                : Component.literal("Unknown");
    }

    public Aspect aspect() {
        return value != null
                ? value.aspect()
                : Aspect.NONE;
    }

    public RGB color() {
        return value != null
                ? value.color()
                : ElixirContents.WATER.color();
    }

    public void bind(@Nullable Essence essence) {
        value = essence;
    }

    static {
        CODEC = ResourceLocation.CODEC.xmap(EssenceHolder::getOrCreate, EssenceHolder::key);
        HOLDERS = Maps.newHashMap();
    }
}
