package dev.obscuria.elixirum.fabric.datagen;

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

final class ModEssenceGenerator implements DataProvider {
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public ModEssenceGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "elixirum/essence");
        this.registryLookup = registryLookup;
    }

    private void generateEssence(HolderLookup.Provider provider, Consumer<Essence> consumer) {
        consumer.accept(new Essence(ElixirumMobEffects.GROW.holder(), amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(ElixirumMobEffects.SHRINK.holder(), amplifier(10, 4), duration(10, 600), 3));

        consumer.accept(new Essence(MobEffects.ABSORPTION, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.BLINDNESS, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.FIRE_RESISTANCE, amplifier(20, 0), duration(10, 1200), 3));
        consumer.accept(new Essence(MobEffects.GLOWING, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.DIG_SPEED, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.HEALTH_BOOST, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.HUNGER, amplifier(20, 4), duration(20, 300), 3));
        consumer.accept(new Essence(MobEffects.HARM, amplifier(20, 4), duration(0, 0), 3));
        consumer.accept(new Essence(MobEffects.HEAL, amplifier(20, 4), duration(0, 0), 3));
        consumer.accept(new Essence(MobEffects.INVISIBILITY, amplifier(20, 0), duration(10, 1200), 3));
        consumer.accept(new Essence(MobEffects.JUMP, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.LEVITATION, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.LUCK, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.DIG_SLOWDOWN, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.CONFUSION, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.NIGHT_VISION, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.POISON, amplifier(20, 4), duration(20, 300), 3));
        consumer.accept(new Essence(MobEffects.REGENERATION, amplifier(20, 4), duration(20, 300), 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_RESISTANCE, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.SATURATION, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.SLOW_FALLING, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SLOWDOWN, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SPEED, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_BOOST, amplifier(20, 4), duration(20, 300), 3));
        consumer.accept(new Essence(MobEffects.UNLUCK, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.WATER_BREATHING, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.WEAKNESS, amplifier(10, 4), duration(10, 600), 3));
        consumer.accept(new Essence(MobEffects.WITHER, amplifier(20, 4), duration(20, 300), 3));
    }

    private Essence.Property amplifier(double minWeight, int maxValue) {
        return new Essence.Property(minWeight, maxValue);
    }

    private Essence.Property duration(double minWeight, int maxValue) {
        return new Essence.Property(minWeight, maxValue);
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
