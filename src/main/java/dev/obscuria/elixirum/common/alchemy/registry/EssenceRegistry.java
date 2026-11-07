package dev.obscuria.elixirum.common.alchemy.registry;

import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class EssenceRegistry {

    private final Map<ResourceLocation, Essence> essences = new HashMap<>();
    private final Set<EssenceHolder> boundHolders = new HashSet<>();

    public final Map<ResourceLocation, Essence> essenceView = Collections.unmodifiableMap(essences);
    public final Set<EssenceHolder> boundHolderView = Collections.unmodifiableSet(boundHolders);

    public boolean contains(ResourceLocation key) {
        return essences.containsKey(key);
    }

    @Nullable
    public Essence get(ResourceLocation key) {
        return essences.get(key);
    }

    public EssenceHolder getHolder(ResourceLocation key) {
        return EssenceHolder.getOrCreate(key);
    }

    public void fill(Map<ResourceLocation, Essence> essences) {
        clear();
        essences.forEach(this::register);
    }

    public void register(ResourceLocation key, Essence essence) {
        this.essences.put(key, essence);
        bind(key, essence);
    }

    public void remove(ResourceLocation key) {
        this.essences.remove(key);
        unbind(key);
    }

    public void clear() {
        this.essences.clear();
        this.boundHolders.forEach(EssenceHolder::unbind);
        this.boundHolders.clear();
    }

    private void bind(ResourceLocation key, Essence essence) {
        var holder = getHolder(key);
        holder.bind(essence);
        this.boundHolders.add(holder);
    }

    private void unbind(ResourceLocation key) {
        var holder = getHolder(key);
        holder.unbind();
        this.boundHolders.remove(holder);
    }
}
