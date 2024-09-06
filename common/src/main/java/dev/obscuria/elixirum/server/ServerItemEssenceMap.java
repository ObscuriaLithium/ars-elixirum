package dev.obscuria.elixirum.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.*;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.Holder;
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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ServerItemEssenceMap extends ItemEssenceMap {
    private static final ImmutableList<Item> BUILT_IN_BLACKLIST;

    void syncWith(ServerPlayer player) {
        Elixirum.PLATFORM.sendToPlayer(player, ClientboundItemEssencesPacket.create(this.pack()));
    }

    public void load() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("essence_map.json");
        this.load(ServerAlchemy.server.registryAccess(), path);
        final var anyDeleted = this.deleteInvalidProperties(ServerAlchemy.server);
        final var anyGenerated = this.generateMissingProperties(ServerAlchemy.server);
        if (anyDeleted || anyGenerated) this.save();
    }

    public void save() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("essence_map.json");
        this.save(ServerAlchemy.server.registryAccess(), path);
    }

    public void regenerate() {
        if (ServerAlchemy.server == null) return;
        this.map.clear();
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
                            ServerAlchemy.LOG.error("Failed to decode essence map");
                            ServerAlchemy.LOG.error(error.message());
                        }));
    }

    private void save(RegistryAccess access, Path path) {
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, access);
        Packed.CODEC.encodeStart(registryOps, this.pack())
                .ifSuccess(element -> ServerAlchemy.trySave(path, element))
                .ifError(error -> {
                    ServerAlchemy.LOG.error("Failed to encode essence map");
                    ServerAlchemy.LOG.error(error.message());
                });
    }

    private boolean deleteInvalidProperties(MinecraftServer server) {
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (!this.map.containsKey(item)) continue;
            if (this.canHoldEssences(item)) continue;
            this.map.remove(item);
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Deleted {} invalid item essences", total);
        return true;
    }

    private boolean generateMissingProperties(MinecraftServer server) {
        final var seed = server.overworld().getSeed();
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (this.map.containsKey(item)) continue;
            if (!this.canHoldEssences(item)) continue;
            this.map.put(item, findPreset(server.registryAccess(), item)
                    .orElseGet(() -> generateProperties(
                            server.registryAccess(),
                            RandomSource.create(seed + item.toString().hashCode()))));
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Generated {} missing item essences", total);
        return true;
    }

    private Optional<ItemEssences> findPreset(RegistryAccess access, Item item) {
        return access.registry(ElixirumRegistries.ESSENCE_PRESET).flatMap(
                registry -> registry.stream()
                        .filter(definition -> definition.target().equals(item))
                        .findFirst().map(preset -> preset.build(access)));
    }

    private ItemEssences generateProperties(RegistryAccess access, RandomSource random) {
        final var essenceCount = 1 + random.nextInt(3);
        var holders = access.registry(ElixirumRegistries.ESSENCE)
                .map(registry -> registry.holders().toList())
                .orElse(List.of());
        if (holders.isEmpty()) return ItemEssences.EMPTY;
        final var essences = Maps.<Holder<Essence>, Integer>newHashMap();
        for (int i = 0; i < essenceCount; i++) {
            final var weight = 1 + random.nextInt(3);
            essences.put(holders.get(random.nextInt(holders.size())), weight);
        }
        return ItemEssences.create(essences);
    }

    private boolean canHoldEssences(Item item) {
        if (BUILT_IN_BLACKLIST.contains(item)) return false;
        if (Essence.isBlacklisted(item)) return false;
        if (Essence.isWhitelisted(item)) return true;
        return !(item instanceof BlockItem block) || block.getBlock() instanceof FlowerBlock;
    }

    static {
        BUILT_IN_BLACKLIST = ImmutableList.<Item>builder()
                .add(Items.AIR)
                .add(Items.POTION)
                .add(Items.SPLASH_POTION)
                .add(Items.LINGERING_POTION)
                .add(Items.GLASS_BOTTLE)
                .build();
    }
}
