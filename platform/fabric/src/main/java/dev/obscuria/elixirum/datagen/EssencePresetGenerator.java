package dev.obscuria.elixirum.datagen;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssencePreset;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

final class EssencePresetGenerator implements DataProvider {
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public EssencePresetGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "elixirum/essence_preset");
        this.registryLookup = registryLookup;
    }

    private void generateEssence(Consumer<ItemEssencePreset> consumer) {

        consumer.accept(ItemEssencePreset.single(Items.ALLIUM, Elixirum.key("fire_resistance"), 1));
        consumer.accept(ItemEssencePreset.single(Items.AZURE_BLUET, Elixirum.key("blindness"), 1));
        consumer.accept(ItemEssencePreset.single(Items.BLUE_ORCHID, Elixirum.key("saturation"), 1));
        consumer.accept(ItemEssencePreset.single(Items.CORNFLOWER, Elixirum.key("jump_boost"), 1));
        consumer.accept(ItemEssencePreset.single(Items.DANDELION, Elixirum.key("saturation"), 1));
        consumer.accept(ItemEssencePreset.single(Items.LILY_OF_THE_VALLEY, Elixirum.key("poison"), 1));
        consumer.accept(ItemEssencePreset.single(Items.ORANGE_TULIP, Elixirum.key("weakness"), 1));
        consumer.accept(ItemEssencePreset.single(Items.PINK_TULIP, Elixirum.key("weakness"), 1));
        consumer.accept(ItemEssencePreset.single(Items.RED_TULIP, Elixirum.key("weakness"), 1));
        consumer.accept(ItemEssencePreset.single(Items.WHITE_TULIP, Elixirum.key("weakness"), 1));
        consumer.accept(ItemEssencePreset.single(Items.OXEYE_DAISY, Elixirum.key("regeneration"), 1));
        consumer.accept(ItemEssencePreset.single(Items.POPPY, Elixirum.key("night_vision"), 1));
        consumer.accept(ItemEssencePreset.single(Items.TORCHFLOWER, Elixirum.key("night_vision"), 1));
        consumer.accept(ItemEssencePreset.single(Items.WITHER_ROSE, Elixirum.key("wither"), 1));

        consumer.accept(ItemEssencePreset.single(Items.BLAZE_POWDER, Elixirum.key("strength"), 2));
        consumer.accept(ItemEssencePreset.single(Items.FERMENTED_SPIDER_EYE, Elixirum.key("weakness"), 2));
        consumer.accept(ItemEssencePreset.single(Items.GHAST_TEAR, Elixirum.key("regeneration"), 2));
        consumer.accept(ItemEssencePreset.single(Items.GLISTERING_MELON_SLICE, Elixirum.key("instant_health"), 2));
        consumer.accept(ItemEssencePreset.single(Items.GOLDEN_CARROT, Elixirum.key("night_vision"), 2));
        consumer.accept(ItemEssencePreset.single(Items.MAGMA_CREAM, Elixirum.key("fire_resistance"), 2));
        consumer.accept(ItemEssencePreset.single(Items.PHANTOM_MEMBRANE, Elixirum.key("slow_falling"), 2));
        consumer.accept(ItemEssencePreset.single(Items.PUFFERFISH, Elixirum.key("water_breathing"), 2));
        consumer.accept(ItemEssencePreset.single(Items.RABBIT_FOOT, Elixirum.key("jump_boost"), 2));
        consumer.accept(ItemEssencePreset.single(Items.SPIDER_EYE, Elixirum.key("poison"), 2));
        consumer.accept(ItemEssencePreset.single(Items.SUGAR, Elixirum.key("speed"), 2));
    }

    @SuppressWarnings("deprecation")
    private ResourceLocation getId(ItemEssencePreset preset) {
        return Elixirum.key(preset.target().builtInRegistryHolder().key().location().getPath());
    }

    private Path getOutputPath(ItemEssencePreset preset) {
        return pathResolver.json(getId(preset));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registryLookup.thenCompose(lookup -> {
            final var identifiers = Sets.<ResourceLocation>newHashSet();
            final var essences = Sets.<ItemEssencePreset>newHashSet();

            generateEssence(essences::add);

            final var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
            final var futures = new ArrayList<CompletableFuture<?>>();

            for (var essence : essences) {
                final var id = getId(essence);
                if (!identifiers.add(id)) {
                    throw new IllegalStateException("Duplicate essence " + id);
                }

                var essenceJson = ItemEssencePreset.DIRECT_CODEC.encodeStart(ops, essence)
                        .getOrThrow(IllegalStateException::new).getAsJsonObject();
                futures.add(DataProvider.saveStable(output, essenceJson, getOutputPath(essence)));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "EssencePresets";
    }
}
