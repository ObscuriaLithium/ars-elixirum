package dev.obscuria.elixirum.api.codex;

import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface AlchemyEssences {

    EssenceHolder getHolder(ResourceLocation key);

    Stream<EssenceHolder> streamHolders();

    void forEachHolder(Consumer<EssenceHolder> consumer);

    void forEach(BiConsumer<ResourceLocation, Essence> consumer);

    Diff generationResult();
}
