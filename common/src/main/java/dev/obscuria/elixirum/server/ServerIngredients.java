package dev.obscuria.elixirum.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.affix.Affix;
import dev.obscuria.elixirum.common.alchemy.affix.AffixType;
import dev.obscuria.elixirum.common.alchemy.essence.*;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientProperties;
import dev.obscuria.elixirum.common.alchemy.ingredient.Ingredients;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FlowerBlock;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ServerIngredients extends Ingredients {
    private static final ImmutableList<Item> BUILTIN_BLACKLIST;

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

    private IngredientProperties generateProperties(RegistryAccess access, RandomSource random) {
        final var essenceCount = 1 + random.nextInt(3);
        var holders = access.registry(ElixirumRegistries.ESSENCE)
                .map(registry -> registry.holders().toList())
                .orElse(List.of());
        if (holders.isEmpty()) return IngredientProperties.EMPTY;
        final var essences = Maps.<ResourceLocation, Integer>newHashMap();
        for (int i = 0; i < essenceCount; i++) {
            final var weight = 1 + random.nextInt(3);
            essences.put(holders.get(random.nextInt(holders.size())).key().location(), weight);
        }
        if (random.nextBoolean()) {
            final var affixType = AffixType.values()[random.nextInt(AffixType.values().length)];
            final var affixModifier = -0.5 + 0.05 * random.nextInt(20);
            return IngredientProperties.create(essences, List.of(new Affix(affixType, affixModifier)));
        } else {
            return IngredientProperties.create(essences, List.of());
        }
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
    }
}
