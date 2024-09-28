package dev.obscuria.elixirum.fabric.datagen;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceCategory;
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
        consumer.accept(new Essence(ElixirumMobEffects.GROW.holder(), EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(ElixirumMobEffects.SHRINK.holder(), EssenceCategory.DIMINISHING, 4, 600, 10, 3));

        consumer.accept(new Essence(MobEffects.ABSORPTION, EssenceCategory.DEFENSIVE, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.BLINDNESS, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.FIRE_RESISTANCE, EssenceCategory.DEFENSIVE, 0, 1200, 10, 3));
        consumer.accept(new Essence(MobEffects.GLOWING, EssenceCategory.NONE, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.DIG_SPEED, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.HEALTH_BOOST, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.HUNGER, EssenceCategory.DIMINISHING, 4, 300, 10, 3));
        consumer.accept(new Essence(MobEffects.HARM, EssenceCategory.OFFENSIVE, 4, 0, 10, 3));
        consumer.accept(new Essence(MobEffects.HEAL, EssenceCategory.DEFENSIVE, 4, 0, 10, 3));
        consumer.accept(new Essence(MobEffects.INVISIBILITY, EssenceCategory.ENHANCING, 0, 1200, 10, 3));
        consumer.accept(new Essence(MobEffects.JUMP, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.LEVITATION, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.LUCK, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.DIG_SLOWDOWN, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.CONFUSION, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.NIGHT_VISION, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.POISON, EssenceCategory.OFFENSIVE, 4, 300, 10, 3));
        consumer.accept(new Essence(MobEffects.REGENERATION, EssenceCategory.DEFENSIVE, 4, 300, 10, 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_RESISTANCE, EssenceCategory.DEFENSIVE, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.SATURATION, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.SLOW_FALLING, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SLOWDOWN, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.MOVEMENT_SPEED, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.DAMAGE_BOOST, EssenceCategory.OFFENSIVE, 4, 300, 10, 3));
        consumer.accept(new Essence(MobEffects.UNLUCK, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.WATER_BREATHING, EssenceCategory.ENHANCING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.WEAKNESS, EssenceCategory.DIMINISHING, 4, 600, 10, 3));
        consumer.accept(new Essence(MobEffects.WITHER, EssenceCategory.OFFENSIVE, 4, 300, 10, 3));
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
