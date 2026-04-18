package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.*;

public final class AlchemyEssencesData {

    public static final Codec<AlchemyEssencesData> CODEC;
    private final Map<ResourceLocation, Essence> source;

    public static AlchemyEssencesData empty() {
        return new AlchemyEssencesData(new HashMap<>());
    }

    public static AlchemyEssencesData copyOf(Map<ResourceLocation, Essence> map) {
        return new AlchemyEssencesData(new HashMap<>(map));
    }

    private AlchemyEssencesData(Map<ResourceLocation, Essence> source) {
        this.source = source;
    }

    public Essence getOrThrow(ResourceLocation key) {
        Essence essence = source.get(key);
        if (essence == null) {
            throw new NoSuchElementException("Essence not found: " + key);
        }
        return essence;
    }

    public Optional<Essence> getOrEmpty(ResourceLocation key) {
        return Optional.ofNullable(source.get(key));
    }

    public Optional<ResourceLocation> keyOf(Essence essence) {
        return source.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), essence))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public boolean containsEffect(Holder.Reference<MobEffect> effect) {
        return source.values().stream().anyMatch(e -> e.effect().is(effect.key()));
    }

    public Map<ResourceLocation, Essence> asMapView() {
        return Collections.unmodifiableMap(source);
    }

    public void clear() {
        source.clear();
    }

    public void putAll(AlchemyEssencesData other) {
        source.putAll(other.source);
    }

    public void put(ResourceLocation key, Essence essence) {
        source.put(key, essence);
    }

    public void remove(ResourceLocation key) {
        source.remove(key);
    }

    public void rebindHolders() {
        EssenceHolder.unbindAll();
        for (var entry : source.entrySet()) {
            var holder = EssenceHolder.getOrCreate(entry.getKey());
            holder.bind(entry.getValue());
        }
    }

    static {
        CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Essence.CODEC)
                .xmap(AlchemyEssencesData::copyOf, it -> it.source);
    }
}
