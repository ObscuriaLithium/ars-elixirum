package dev.obscuria.elixirum.server;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.network.ClientboundDiscoverPacket;
import dev.obscuria.elixirum.network.ClientboundProfilePacket;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.*;

public final class ServerElixirumProfile extends ElixirumProfile {
    private final ServerPlayer player;

    public ServerElixirumProfile(ServerPlayer player) {
        this.player = player;
    }

    public void syncWithPlayer() {
        Elixirum.PLATFORM.sendToPlayer(player, ClientboundProfilePacket.create(this.pack()));
    }

    public List<ElixirHolder> getSavedPages() {
        return new ArrayList<>(savedPages);
    }

    public boolean savePage(ElixirHolder page) {
        return savedPages.add(page);
    }

    public boolean removePage(ElixirHolder page) {
        return savedPages.remove(page);
    }

    public Set<Holder<Essence>> getDiscoveredEssences(Item item) {
        return Util.make(Sets.newHashSet(), set -> Optional
                .ofNullable(discoveredEssences.get(item))
                .ifPresent(set::addAll));
    }

    public boolean isDiscovered(Item item, Holder<Essence> essence) {
        final var essences = discoveredEssences.get(item);
        return essences != null && essences.contains(essence);
    }

    public void discoverEssence(Item item, Holder<Essence> essence, boolean sync) {
        discoveredEssences.compute(item, (key, value) -> Util.make(
                value == null ? Sets.newHashSet() : value,
                set -> {
                    if (!set.add(essence) || !sync) return;
                    Elixirum.PLATFORM.sendToPlayer(player, ClientboundDiscoverPacket.create(item, essence));
                }));
    }

    public void forgetEssence(Item item, Holder<Essence> essence, boolean sync) {
        discoveredEssences.computeIfPresent(item, (key, value) -> {
            value.remove(essence);
            return value.isEmpty() ? null : value;
        });
    }

    void load() {
        if (ServerAlchemy.server == null) return;
        final var path = getProfilePath(ServerAlchemy.server);
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, ServerAlchemy.server.registryAccess());
        ServerAlchemy.tryLoad(path)
                .ifPresent(element -> ElixirumProfile.CODEC.decode(registryOps, element)
                        .ifSuccess(pair -> this.unpack(pair.getFirst()))
                        .ifError(error -> {
                            ServerAlchemy.LOG.warn("Failed to decode player profile");
                            ServerAlchemy.LOG.warn(error.message());
                        }));
    }

    void save() {
        if (ServerAlchemy.server == null) return;
        final var path = getProfilePath(ServerAlchemy.server);
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, ServerAlchemy.server.registryAccess());
        ElixirumProfile.CODEC.encodeStart(registryOps, this.pack())
                .ifSuccess(element -> ServerAlchemy.trySave(path, element))
                .ifError(error -> {
                    ServerAlchemy.LOG.warn("Failed to encode player profile");
                    ServerAlchemy.LOG.warn(error.message());
                });
    }

    private Path getProfilePath(MinecraftServer server) {
        return server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("profiles/" + player.getUUID() + ".json");
    }
}
