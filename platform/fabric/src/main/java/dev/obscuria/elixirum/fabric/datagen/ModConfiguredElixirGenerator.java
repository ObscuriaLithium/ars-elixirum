package dev.obscuria.elixirum.fabric.datagen;

import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ConfiguredElixir;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumEssences;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

final class ModConfiguredElixirGenerator implements DataProvider {
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public ModConfiguredElixirGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "elixirum/configured_elixir");
        this.registryLookup = registryLookup;
    }

    private void generateEssence(HolderLookup.Provider provider, BiConsumer<String, ConfiguredElixir> consumer) {
        consumer.accept("health_boost", ConfiguredElixir.create(
                ConfiguredElixir.variant(template(ElixirumEssences.HEALTH_BOOST, 0, 60)),
                ConfiguredElixir.variant(template(ElixirumEssences.HEALTH_BOOST, 2, 180)),
                ConfiguredElixir.variant(template(ElixirumEssences.HEALTH_BOOST, 5, 360))));
    }

    private ConfiguredElixir.Variant.Template template(ResourceKey<Essence> essence,
                                                       int amplifier, int duration) {
        return ConfiguredElixir.template(essence.location(), amplifier, duration);
    }

    private Path getOutputPath(ResourceLocation id) {
        return pathResolver.json(id);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registryLookup.thenCompose(lookup -> {
            final var values = Maps.<String, ConfiguredElixir>newHashMap();
            generateEssence(lookup, values::put);

            final var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
            final var futures = new ArrayList<CompletableFuture<?>>();

            values.forEach((key, value) -> {
                var json = ConfiguredElixir.DIRECT_CODEC
                        .encodeStart(ops, value)
                        .getOrThrow(IllegalStateException::new)
                        .getAsJsonObject();
                final var path = getOutputPath(Elixirum.key(key));
                futures.add(DataProvider.saveStable(output, json, path));
            });

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Configured Elixirs";
    }
}
