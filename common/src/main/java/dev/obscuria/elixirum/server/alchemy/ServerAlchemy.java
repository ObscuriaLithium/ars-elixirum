package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.network.ClientboundAlchemyPayload;
import dev.obscuria.elixirum.common.network.ClientboundDiffPayload;
import dev.obscuria.elixirum.config.GenerationConfig;
import dev.obscuria.elixirum.server.ServerExtensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ServerAlchemy implements Alchemy {

    private final MinecraftServer server;
    private final ServerAlchemyEssences essences = new ServerAlchemyEssences();
    private final ServerAlchemyIngredients ingredients = new ServerAlchemyIngredients();
    private final Map<UUID, ServerAlchemyProfile> profiles = new HashMap<>();

    public static ServerAlchemy get(MinecraftServer server) {
        return ((ServerExtensions) server).elixirum$getAlchemy();
    }

    public ServerAlchemy(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public ServerAlchemyEssences essences() {
        return essences;
    }

    @Override
    public ServerAlchemyIngredients ingredients() {
        return ingredients;
    }

    @Override
    public ServerAlchemyProfile profileOf(Player player) {
        return getOrCreateProfile(player);
    }

    public void onServerStart() {
        GenerationConfig.refresh();
        this.essences.load(server);
        this.ingredients.load(server);
        this.ingredients.reconcile();
    }

    public void onServerSave() {
        this.essences.save(server);
        this.ingredients.save(server);
        profiles.values().forEach(ServerAlchemyProfile::save);
    }

    public void onServerStop() {
        EssenceHolder.clearCache();
    }

    public void onPlayerJoin(ServerPlayer player) {
        var profile = getOrCreateProfile(player);
        profile.load();
        profile.sendToClient(new ClientboundAlchemyPayload(
                Optional.of(essences.pack()),
                Optional.of(ingredients.pack()),
                Optional.of(profile.pack())));
        profile.sendToClient(3, new ClientboundDiffPayload(
                essences.generationResult(),
                ingredients.generationResult()));
    }

    public void onPlayerLeave(ServerPlayer player) {
        var profile = profiles.remove(player.getUUID());
        if (profile == null) return;
        profile.save();
    }

    private ServerAlchemyProfile getOrCreateProfile(Player player) {
        return profiles.computeIfAbsent(player.getUUID(), this::createProfile);
    }

    private ServerAlchemyProfile createProfile(UUID uuid) {
        return new ServerAlchemyProfile(server, uuid);
    }
}
