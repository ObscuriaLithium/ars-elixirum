package dev.obscuria.elixirum.fabric.datagen;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirPrefix;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

final class ModElixirPrefixGenerator implements DataProvider {
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public ModElixirPrefixGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "elixirum/elixir_prefix");
        this.registryLookup = registryLookup;
    }

    private void generateEssence(BiConsumer<ResourceLocation, ElixirPrefix> consumer) {
        DefaultPrefixes.acceptPrefixes((id, source, key) -> consumer.accept(id, new ElixirPrefix(source, key)));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registryLookup.thenCompose(lookup -> {
            final var identifiers = Sets.<ResourceLocation>newHashSet();
            final var prefixes = Maps.<ResourceLocation, ElixirPrefix>newHashMap();

            generateEssence(prefixes::put);

            final var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
            final var futures = new ArrayList<CompletableFuture<?>>();

            for (var entry : prefixes.entrySet()) {
                if (!identifiers.add(entry.getKey())) {
                    throw new IllegalStateException("Duplicate prefix " + entry.getKey());
                }

                var essenceJson = ElixirPrefix.DIRECT_CODEC.encodeStart(ops, entry.getValue())
                        .getOrThrow(IllegalStateException::new).getAsJsonObject();
                futures.add(DataProvider.saveStable(output, essenceJson, pathResolver.json(entry.getKey())));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Elixir Presets";
    }
}
