package dev.obscuria.elixirum.datagen;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumMobEffects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

final class EssenceGenerator implements DataProvider {
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public EssenceGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "elixirum/essence");
        this.registryLookup = registryLookup;
    }

    private void generateEssence(HolderLookup.Provider registryLookup, Consumer<Essence> consumer) {
        consumer.accept(new Essence(ElixirumMobEffects.GROW.holder(), 3));
        consumer.accept(new Essence(ElixirumMobEffects.SHRINK.holder(), 3));

        consumer.accept(new Essence(MobEffects.ABSORPTION, 3));
        consumer.accept(new Essence(MobEffects.BLINDNESS, 3));
        consumer.accept(new Essence(MobEffects.FIRE_RESISTANCE, 3));
        consumer.accept(new Essence(MobEffects.GLOWING, 3));
        consumer.accept(new Essence(MobEffects.DIG_SPEED, 3));
        consumer.accept(new Essence(MobEffects.HEALTH_BOOST, 3));
        consumer.accept(new Essence(MobEffects.HUNGER, 3));
        consumer.accept(new Essence(MobEffects.HARM, 3));
        consumer.accept(new Essence(MobEffects.HEAL, 3));
        consumer.accept(new Essence(MobEffects.INVISIBILITY, 3));
        consumer.accept(new Essence(MobEffects.JUMP, 3));
        consumer.accept(new Essence(MobEffects.LEVITATION, 3));
        consumer.accept(new Essence(MobEffects.LUCK, 3));
        consumer.accept(new Essence(MobEffects.DIG_SLOWDOWN, 3));
        consumer.accept(new Essence(MobEffects.CONFUSION, 3));
        consumer.accept(new Essence(MobEffects.NIGHT_VISION, 3));
        consumer.accept(new Essence(MobEffects.POISON, 3));
        consumer.accept(new Essence(MobEffects.REGENERATION, 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_RESISTANCE, 3));
        consumer.accept(new Essence(MobEffects.SATURATION, 3));
        consumer.accept(new Essence(MobEffects.SLOW_FALLING, 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SLOWDOWN, 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SPEED, 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_BOOST, 3));
        consumer.accept(new Essence(MobEffects.UNLUCK, 3));
        consumer.accept(new Essence(MobEffects.WATER_BREATHING, 3));
        consumer.accept(new Essence(MobEffects.WEAKNESS, 3));
        consumer.accept(new Essence(MobEffects.WITHER, 3));
    }

    private ResourceLocation getId(Essence essence) {
        return Elixirum.key(essence.effectHolder().unwrapKey().orElseThrow().location().getPath());
    }

    private Path getOutputPath(Essence essence) {
        return pathResolver.json(getId(essence));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registryLookup.thenCompose(lookup -> {
            final var identifiers = Sets.<ResourceLocation>newHashSet();
            final var essences = Sets.<Essence>newHashSet();

            generateEssence(lookup, essences::add);

            final var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
            final var futures = new ArrayList<CompletableFuture<?>>();

            for (var essence : essences) {
                final var id = getId(essence);
                if (!identifiers.add(id)) {
                    throw new IllegalStateException("Duplicate essence " + id);
                }

                var essenceJson = Essence.DIRECT_CODEC.encodeStart(ops, essence).getOrThrow(IllegalStateException::new).getAsJsonObject();
                futures.add(DataProvider.saveStable(output, essenceJson, getOutputPath(essence)));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Essences";
    }
}
