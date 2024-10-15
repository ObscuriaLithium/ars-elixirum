package dev.obscuria.elixirum.server;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.network.ClientboundDiscoverPayload;
import dev.obscuria.elixirum.network.ClientboundProfilePayload;
import dev.obscuria.elixirum.network.ServerboundCollectionActionPayload;
import dev.obscuria.core.api.v1.common.ObscureNetworking;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ServerElixirumProfile extends ElixirumProfile
{
    private final ServerPlayer player;

    public ServerElixirumProfile(ServerPlayer player)
    {
        this.player = player;
    }

    public void syncWithPlayer()
    {
        ObscureNetworking.sendTo(player, ClientboundProfilePayload.create(this.pack()));
    }

    public List<ElixirHolder> getCollection()
    {
        return new ArrayList<>(collection);
    }

    public Optional<ElixirHolder> searchInCollection(ElixirRecipe recipe)
    {
        for (var holder : getCollection())
            if (holder.is(recipe))
                return Optional.of(holder);
        return Optional.empty();
    }

    public boolean isOnCollection(ElixirRecipe recipe)
    {
        return collection.stream().anyMatch(holder -> holder.is(recipe));
    }

    public boolean addToCollection(ElixirHolder holder)
    {
        if (isOnCollection(holder.getRecipe())) return false;
        collection.add(holder);
        return true;
    }

    public boolean updateInCollection(ElixirHolder holder)
    {
        if (!isOnCollection(holder.getRecipe())) return false;
        collection.replaceAll(saved -> saved.isSame(holder) ? holder : saved);
        return true;
    }

    public boolean removeFromCollection(ElixirHolder holder)
    {
        if (!isOnCollection(holder.getRecipe())) return false;
        collection.remove(holder);
        return true;
    }

    public Set<Holder<Essence>> getDiscoveredEssences(Item item)
    {
        return Util.make(Sets.newHashSet(), set -> Optional
                .ofNullable(discoveredEssences.get(item))
                .ifPresent(set::addAll));
    }

    public boolean isEssenceDiscovered(Item item, Holder<Essence> essence)
    {
        final var essences = discoveredEssences.get(item);
        return essences != null && essences.contains(essence);
    }

    public void discoverEssence(Item item, Holder<Essence> essence, boolean sync)
    {
        this.discoveredEssences.compute(item, (key, value) -> Util.make(
                value == null ? Sets.newHashSet() : value,
                set -> {
                    if (!set.add(essence) || !sync) return;
                    ObscureNetworking.sendTo(player, ClientboundDiscoverPayload.create(item, essence));
                }));
    }

    public void forgetEssence(Item item, Holder<Essence> essence)
    {
        this.discoveredEssences.computeIfPresent(item, (key, value) -> {
            value.remove(essence);
            return value.isEmpty() ? null : value;
        });
    }

    public boolean validate()
    {
        final var anyChanged = new AtomicBoolean(false);
        this.discoveredEssences.keySet().forEach(item ->
                this.discoveredEssences.computeIfPresent(item, (key, value) -> {
                    final var essences = ServerAlchemy.getIngredients().getProperties(item).getEssences().keySet();
                    value.removeIf(essence -> {
                        if (!(essence instanceof Holder.Reference<Essence> reference)) return false;
                        if (essences.contains(reference.key().location())) return false;
                        anyChanged.set(true);
                        return true;
                    });
                    return value.isEmpty() ? null : value;
                }));
        return anyChanged.get();
    }

    @ApiStatus.Internal
    public void handle(ServerboundCollectionActionPayload packet)
    {
        switch (packet.action())
        {
            case ADD -> addToCollection(packet.holder());
            case UPDATE -> updateInCollection(packet.holder());
            case REMOVE -> removeFromCollection(packet.holder());
        }
    }

    void load()
    {
        if (ServerAlchemy.server == null) return;
        final var path = getProfilePath(ServerAlchemy.server);
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, ServerAlchemy.server.registryAccess());
        ServerAlchemy.tryLoad(path)
                .ifPresent(element -> ElixirumProfile.CODEC.decode(registryOps, element)
                        .ifSuccess(pair -> this.unpack(pair.getFirst()))
                        .ifError(error -> {
                            ServerAlchemy.LOG.warn("Failed to decode profile");
                            ServerAlchemy.LOG.warn(error.message());
                        }));
        this.validate();
    }

    void save()
    {
        if (ServerAlchemy.server == null) return;
        final var path = getProfilePath(ServerAlchemy.server);
        final var registryOps = RegistryOps.create(JsonOps.INSTANCE, ServerAlchemy.server.registryAccess());
        ElixirumProfile.CODEC.encodeStart(registryOps, this.pack())
                .ifSuccess(element -> ServerAlchemy.trySave(path, element))
                .ifError(error -> {
                    ServerAlchemy.LOG.warn("Failed to encode profile");
                    ServerAlchemy.LOG.warn(error.message());
                });
    }

    private Path getProfilePath(MinecraftServer server)
    {
        return server.getWorldPath(ServerAlchemy.ALCHEMY_DIR).resolve("profiles/" + player.getUUID() + ".json");
    }
}
