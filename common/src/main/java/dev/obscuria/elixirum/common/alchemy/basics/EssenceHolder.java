package dev.obscuria.elixirum.common.alchemy.basics;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

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

    public Essence require() {
        if (value == null) {
            throw new IllegalStateException("Trying to access unbound essence '%s'".formatted(key));
        }
        return value;
    }

    public Optional<Essence> optional() {
        return Optional.ofNullable(value);
    }

    public void bind(@Nullable Essence essence) {
        value = essence;
    }

    static {
        CODEC = ResourceLocation.CODEC.xmap(EssenceHolder::getOrCreate, EssenceHolder::key);
        HOLDERS = Maps.newHashMap();
    }
}
