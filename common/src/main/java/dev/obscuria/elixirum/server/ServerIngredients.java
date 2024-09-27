package dev.obscuria.elixirum.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.affix.AffixType;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientProperties;
import dev.obscuria.elixirum.common.alchemy.ingredient.Ingredients;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FlowerBlock;
import org.apache.commons.compress.utils.Lists;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ServerIngredients extends Ingredients {
    private static final ImmutableList<Item> BUILTIN_BLACKLIST;
    private static final ImmutableMap<GenerationScenario, Integer> SCENARIOS;

    void syncWithPlayer(ServerPlayer player) {
        Elixirum.PLATFORM.sendToPlayer(player, ClientboundItemEssencesPacket.create(this.pack()));
    }

    public void load() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("ingredients.map");
        this.load(ServerAlchemy.server.registryAccess(), path);
        final var anyDeleted = this.deleteInvalidProperties(ServerAlchemy.server);
        final var anyGenerated = this.generateMissingProperties(ServerAlchemy.server);
        if (anyDeleted || anyGenerated) {
            this.save();
            this.computeTotalEssences();
        }
    }

    public void save() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("ingredients.map");
        this.save(ServerAlchemy.server.registryAccess(), path);
    }

    public void regenerate() {
        if (ServerAlchemy.server == null) return;
        this.properties.clear();
        this.generateMissingProperties(ServerAlchemy.server);
        this.save();
    }

    @Override
    protected void whenExternallyModified() {
        this.save();
    }

    private void load(RegistryAccess access, Path path) {
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, access);
        ServerAlchemy.tryLoad(path)
                .ifPresent(element -> Packed.CODEC.decode(registryOps, element)
                        .ifSuccess(pair -> this.unpack(pair.getFirst()))
                        .ifError(error -> {
                            ServerAlchemy.LOG.error("Failed to decode ingredients");
                            ServerAlchemy.LOG.error(error.message());
                        }));
    }

    private void save(RegistryAccess access, Path path) {
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, access);
        Packed.CODEC.encodeStart(registryOps, this.pack())
                .ifSuccess(element -> ServerAlchemy.trySave(path, element))
                .ifError(error -> {
                    ServerAlchemy.LOG.error("Failed to encode ingredients");
                    ServerAlchemy.LOG.error(error.message());
                });
    }

    private boolean deleteInvalidProperties(MinecraftServer server) {
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (!this.properties.containsKey(item)) continue;
            if (this.shouldBeIngredient(item)) continue;
            this.properties.remove(item);
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Deleted {} invalid ingredients", total);
        return true;
    }

    private boolean generateMissingProperties(MinecraftServer server) {
        final var seed = server.overworld().getSeed();
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (this.properties.containsKey(item)) continue;
            if (!this.shouldBeIngredient(item)) continue;
            this.properties.put(item, findPreset(server.registryAccess(), item)
                    .orElseGet(() -> generateProperties(
                            server.registryAccess(),
                            RandomSource.create(seed + item.toString().hashCode()))));
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Generated {} missing ingredients", total);
        return true;
    }

    private Optional<IngredientProperties> findPreset(RegistryAccess access, Item item) {
        return access.registry(ElixirumRegistries.INGREDIENT_PRESET)
                .flatMap(registry -> registry.stream()
                        .filter(definition -> definition.target().equals(item))
                        .findFirst().map(preset -> preset.build(access)));
    }

    private IngredientProperties generateProperties(RegistryAccess access, RandomSource source) {
        final var lookup = access.lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var scenario = pickRandomScenario(source);
        return scenario.generate(lookup, source);
    }

    private boolean shouldBeIngredient(Item item) {
        if (BUILTIN_BLACKLIST.contains(item)) return false;
        if (Essence.isBlacklisted(item)) return false;
        if (Essence.isWhitelisted(item)) return true;
        return !(item instanceof BlockItem block) || block.getBlock() instanceof FlowerBlock;
    }

    static {
        BUILTIN_BLACKLIST = ImmutableList.<Item>builder()
                .add(Items.AIR)
                .add(Items.POTION)
                .add(Items.SPLASH_POTION)
                .add(Items.LINGERING_POTION)
                .add(Items.GLASS_BOTTLE)
                .build();
        SCENARIOS = ImmutableMap.<GenerationScenario, Integer>builder()
                .put(ServerIngredients::generateSimple, 50)
                .put(ServerIngredients::generateMediumWithIngredientBuff, 14)
                .put(ServerIngredients::generateStrongWithIngredientDebuff, 14)
                .put(ServerIngredients::generateMediumWithEssenceBuff, 8)
                .put(ServerIngredients::generateStrongWithEssenceDebuff, 8)
                .put(ServerIngredients::generateWeakWithAbsoluteBuff, 6)
                .build();
    }

    private interface GenerationScenario {

        IngredientProperties generate(HolderLookup<Essence> lookup, RandomSource source);
    }

    private static GenerationScenario pickRandomScenario(RandomSource source) {
        final var totalWeight = SCENARIOS.values().stream().reduce(0, Integer::sum);
        final var randomValue = source.nextInt(totalWeight);
        var currentWeight = 0;
        for (Map.Entry<GenerationScenario, Integer> entry : SCENARIOS.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight)
                return entry.getKey();
        }
        throw new IllegalStateException();
    }

    private static Stream<Holder.Reference<Essence>> pickRandomEssences(HolderLookup<Essence> lookup, RandomSource source, int amount) {
        final var essences = lookup.listElements().collect(Collectors.toList());
        if (amount > essences.size()) amount = essences.size();
        final var result = Lists.<Holder.Reference<Essence>>newArrayList();
        for (int i = 0; i < amount; i++) {
            int index = source.nextInt(essences.size());
            result.add(essences.remove(index));
        }
        return result.stream();
    }

    private static IngredientProperties generateSimple(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(2, 9))),
                List.of());
    }

    private static IngredientProperties generateWeakWithAbsoluteBuff(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(1, 5))),
                List.of(AffixType.ABSOLUTE.create(0.01 * source.nextInt(10, 31))));
    }

    private static IngredientProperties generateMediumWithIngredientBuff(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(2, 9))),
                List.of(AffixType.pickIngredientBound(source).create(0.01 * source.nextInt(10, 31))));
    }

    private static IngredientProperties generateMediumWithEssenceBuff(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(2, 9))),
                List.of(AffixType.pickEssenceBound(source).create(0.01 * source.nextInt(10, 31))));
    }

    private static IngredientProperties generateStrongWithIngredientDebuff(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(8, 17))),
                List.of(AffixType.pickIngredientBound(source).create(0.01 * source.nextInt(-30, -9))));
    }

    private static IngredientProperties generateStrongWithEssenceDebuff(HolderLookup<Essence> lookup, RandomSource source) {
        return IngredientProperties.create(
                pickRandomEssences(lookup, source, source.nextInt(1, 4))
                        .collect(Collectors.toMap(
                                holder -> holder.key().location(),
                                holder -> source.nextInt(8, 17))),
                List.of(AffixType.pickEssenceBound(source).create(0.01 * source.nextInt(-30, -9))));
    }
}
