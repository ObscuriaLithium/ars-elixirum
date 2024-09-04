package dev.obscuria.elixirum.server;

import com.google.common.collect.Lists;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.properties.AlchemyPropertyMap;
import dev.obscuria.elixirum.common.alchemy.properties.AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceStack;
import dev.obscuria.elixirum.common.alchemy.properties.PropertyDefinition;
import dev.obscuria.elixirum.network.S2CAlchemyMapMessage;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ServerAlchemyPropertyMap extends AlchemyPropertyMap {

    void syncWith(ServerPlayer player) {
        Elixirum.PLATFORM.sendToClient(player, S2CAlchemyMapMessage.create(this.pack()));
    }

    public void load() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("properties.json");
        this.load(ServerAlchemy.server.registryAccess(), path);
        final var anyDeleted = this.deleteInvalidProperties(ServerAlchemy.server);
        final var anyGenerated = this.generateMissingProperties(ServerAlchemy.server);
        if (anyDeleted || anyGenerated) this.save();
    }

    public void save() {
        if (ServerAlchemy.server == null) return;
        final var path = ServerAlchemy.server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("properties.json");
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
                            ServerAlchemy.LOG.error("Failed to decode alchemy map");
                            ServerAlchemy.LOG.error(error.message());
                        }));
    }

    private void save(RegistryAccess access, Path path) {
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, access);
        Packed.CODEC.encodeStart(registryOps, this.pack())
                .ifSuccess(element -> ServerAlchemy.trySave(path, element))
                .ifError(error -> {
                    ServerAlchemy.LOG.error("Failed to encode alchemy map");
                    ServerAlchemy.LOG.error(error.message());
                });
    }

    private boolean deleteInvalidProperties(MinecraftServer server) {
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (!this.properties.containsKey(item)) continue;
            if (this.isPropertyFriendly(item)) continue;
            this.properties.remove(item);
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Deleted {} invalid properties", total);
        return true;
    }

    private boolean generateMissingProperties(MinecraftServer server) {
        final var seed = server.overworld().getSeed();
        var total = 0;

        for (var item : BuiltInRegistries.ITEM) {
            if (this.properties.containsKey(item)) continue;
            if (!this.isPropertyFriendly(item)) continue;
            this.properties.put(item, findDefinition(server.registryAccess(), item)
                    .orElseGet(() -> generateProperties(
                            server.registryAccess(),
                            RandomSource.create(seed + item.toString().hashCode()))));
            total += 1;
        }

        if (total <= 0) return false;
        ServerAlchemy.LOG.info("Generated {} missing properties", total);
        return true;
    }

    private Optional<AlchemyProperties> findDefinition(RegistryAccess access, Item item) {
        return access.registry(ElixirumRegistries.PROPERTY_DEFINITION).flatMap(
                registry -> registry.stream()
                        .filter(definition -> definition.target().equals(item))
                        .findFirst().map(PropertyDefinition::properties));
    }

    private AlchemyProperties generateProperties(RegistryAccess access, RandomSource random) {
        final var essenceCount = 1 + random.nextInt(3);
        var holders = access.registry(ElixirumRegistries.ESSENCE)
                .map(registry -> registry.holders().toList())
                .orElse(List.of());
        if (holders.isEmpty()) return AlchemyProperties.EMPTY;
        final var essences = Lists.<EssenceStack>newArrayList();
        for (int i = 0; i < essenceCount; i++) {
            final var weight = 1 + random.nextInt(3);
            essences.add(new EssenceStack(holders.get(random.nextInt(holders.size())), weight));
        }
        return AlchemyProperties.create(essences);
    }

    private boolean isPropertyFriendly(Item item) {
        if (item == Items.AIR) return false;
//        if (item.getClass().isAnnotationPresent(PropertyUnfriendly.class)
//                || item.builtInRegistryHolder().is(Wunschpunsch.PROPERTY_UNFRIENDLY)) return false;
//        if (item.getClass().isAnnotationPresent(PropertyFriendly.class)
//                || item.builtInRegistryHolder().is(Wunschpunsch.PROPERTY_FRIENDLY)) return true;
//        if (item instanceof BlockItem blockItem) return blockItem instanceof IPlantable;
        return true;
    }
}
