package dev.obscuria.elixirum.common.alchemy.codex;

import dev.obscuria.elixirum.api.codex.AlchemyEssences;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AbstractAlchemyEssences implements AlchemyEssences {

    protected final Map<ResourceLocation, Essence> essenceMap = new HashMap<>();
    protected final Set<EssenceHolder> holderSet = new HashSet<>();

    public void unpack(PackedAlchemyEssences packed) {
        EssenceHolder.unbindAll();
        this.essenceMap.clear();
        this.holderSet.clear();
        packed.essences().forEach(this::register);
    }

    public PackedAlchemyEssences pack() {
        return new PackedAlchemyEssences(essenceMap);
    }

    public void register(ResourceLocation key, Essence essence) {
        this.essenceMap.put(key, essence);
        this.registerHolder(key, essence);
    }

    public void registerHolder(ResourceLocation key, Essence essence) {
        var holder = EssenceHolder.getOrCreate(key);
        holder.bind(essence);
        this.holderSet.add(holder);
    }

    @Override
    public EssenceHolder getHolder(ResourceLocation key) {
        return EssenceHolder.getOrCreate(key);
    }

    @Override
    public Stream<EssenceHolder> streamHolders() {
        return holderSet.stream();
    }

    @Override
    public void forEachHolder(Consumer<EssenceHolder> consumer) {
        this.holderSet.forEach(consumer);
    }

    @Override
    public void forEach(BiConsumer<ResourceLocation, Essence> consumer) {
        this.essenceMap.forEach(consumer);
    }
}
