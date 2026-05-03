package dev.obscuria.elixirum.common.alchemy.codex;

import dev.obscuria.elixirum.api.codex.AlchemyEssences;
import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AbstractAlchemyEssences implements AlchemyEssences {

    protected final EssenceRegistry registry = new EssenceRegistry();
    protected Diff generationResult = Diff.empty();

    public void unpack(PackedAlchemyEssences packed) {
        this.registry.fill(packed.essences());
    }

    public PackedAlchemyEssences pack() {
        return new PackedAlchemyEssences(registry.essenceView);
    }

    public Diff generationResult() {
        return generationResult;
    }

    @Override
    public EssenceHolder getHolder(ResourceLocation key) {
        return registry.getHolder(key);
    }

    @Override
    public Stream<EssenceHolder> streamHolders() {
        return registry.boundHolderView.stream();
    }

    @Override
    public void forEachHolder(Consumer<EssenceHolder> consumer) {
        this.registry.boundHolderView.forEach(consumer);
    }

    @Override
    public void forEach(BiConsumer<ResourceLocation, Essence> consumer) {
        this.registry.essenceView.forEach(consumer);
    }
}
