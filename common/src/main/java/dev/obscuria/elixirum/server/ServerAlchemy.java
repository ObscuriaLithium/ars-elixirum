package dev.obscuria.elixirum.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssences;
import net.minecraft.FileUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
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
import java.util.Optional;

public final class ServerAlchemy {
    static final LevelResource ALCHEMY_DIR = createResource();
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Server");
    static final ServerItemEssenceMap properties = new ServerItemEssenceMap();
    static @Nullable MinecraftServer server;

    public static ServerItemEssenceMap getPropertyMap() {
        return properties;
    }

    public static boolean hasProperties(Item item) {
        return properties.hasProperties(item);
    }

    public static ItemEssences getProperties(Item item) {
        return properties.getProperties(item);
    }

    public static void syncPropertyMap() {
        if (server == null) return;
        for (var player : server.getPlayerList().getPlayers())
            properties.syncWith(player);
    }

    @ApiStatus.Internal
    public static void whenServerStarted(MinecraftServer server) {
        ServerAlchemy.server = server;
        properties.load();
    }

    @ApiStatus.Internal
    public static void whenResourcesReloaded(MinecraftServer server) {
        properties.load();
        for (var player : server.getPlayerList().getPlayers())
            properties.syncWith(player);
    }

    @ApiStatus.Internal
    public static void whenServerSaved(MinecraftServer server) {
        properties.save();
    }

    @ApiStatus.Internal
    public static void whenServerStopped(MinecraftServer server) {
        properties.save();
        ServerAlchemy.server = null;
    }

    @ApiStatus.Internal
    public static void whenPlayerLoggedIn(ServerPlayer player) {
        properties.syncWith(player);
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
        } catch (IOException ignored) {
        }
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
