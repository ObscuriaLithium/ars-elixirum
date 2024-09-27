package dev.obscuria.elixirum.server;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.network.ServerboundCollectionActionPacket;
import dev.obscuria.elixirum.network.ServerboundProfilePacket;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ServerAlchemy {
    static final LevelResource ALCHEMY_DIR = createResource();
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Server");
    static final ServerIngredients ingredients = new ServerIngredients();
    static final Map<UUID, ServerElixirumProfile> playerProfiles = Maps.newHashMap();
    static @Nullable MinecraftServer server;

    public static ServerIngredients getIngredients() {
        return ingredients;
    }

    public static ServerElixirumProfile getProfile(ServerPlayer player) {
        return playerProfiles.get(player.getUUID());
    }

    public static void syncIngredients() {
        if (server == null) return;
        for (var player : server.getPlayerList().getPlayers())
            ingredients.syncWithPlayer(player);
    }

    public static void validateProfiles() {
        if (server == null) return;
        for (var profile : playerProfiles.values())
            if (profile.validate())
                profile.syncWithPlayer();
    }

    @ApiStatus.Internal
    public static void whenServerStarted(MinecraftServer server) {
        ServerAlchemy.server = server;
        ingredients.load();
    }

    @ApiStatus.Internal
    public static void whenResourcesReloaded(MinecraftServer server) {
        ingredients.load();
        for (var player : server.getPlayerList().getPlayers())
            ingredients.syncWithPlayer(player);
    }

    @ApiStatus.Internal
    public static void whenServerSaved(MinecraftServer server) {
        ingredients.save();
    }

    @ApiStatus.Internal
    public static void whenServerStopped(MinecraftServer server) {
        ingredients.save();
        server.getPlayerList().getPlayers().forEach(ServerAlchemy::unregisterPlayer);
        ServerAlchemy.server = null;
    }

    @ApiStatus.Internal
    public static void registerPlayer(ServerPlayer player) {
        ingredients.syncWithPlayer(player);
        playerProfiles.compute(player.getUUID(), (key, value) -> Util.make(
                value == null ? new ServerElixirumProfile(player) : value,
                profile -> {
                    profile.load();
                    profile.syncWithPlayer();
                }));
    }

    @ApiStatus.Internal
    public static void unregisterPlayer(ServerPlayer player) {
        playerProfiles.compute(player.getUUID(), (key, value) -> {
            if (value != null) value.save();
            return null;
        });
    }

    @ApiStatus.Internal
    public static Optional<JsonElement> tryLoad(Path path) {
        if (Files.isRegularFile(path)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                return Optional.of(Streams.parse(reader));
            } catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    @ApiStatus.Internal
    public static void trySave(Path path, JsonElement element) {
        try {
            FileUtil.createDirectoriesSafe(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(element, writer);
            }
        } catch (IOException ignored) {}
    }

    @ApiStatus.Internal
    public static void handle(ServerboundProfilePacket packet, ServerPlayer player) {
        getProfile(player).unpackCollection(packet.content());
    }

    @ApiStatus.Internal
    public static void handle(ServerboundCollectionActionPacket packet, ServerPlayer player) {
        getProfile(player).handle(packet);
    }

    private static LevelResource createResource() {
        try {
            var constructor = LevelResource.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance("obscuria/alchemy");
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException e) {
            throw new RuntimeException("Failed to create LevelResource instance.", e);
        }
    }
}
