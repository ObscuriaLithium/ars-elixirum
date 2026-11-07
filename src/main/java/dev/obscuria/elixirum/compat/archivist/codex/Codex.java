//? if >=1.21 {
/*package dev.obscuria.elixirum.compat.archivist.codex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

public final class Codex {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String directory;

    private Codex(String directory) {
        this.directory = relativePath(directory);
    }

    public static Builder createBuilder(String directory) {
        return new Builder(new Codex(directory));
    }

    static String relativePath(String path) {
        if (path == null || !path.matches("[a-zA-Z0-9_-]+(?:/[a-zA-Z0-9_-]+)*")) {
            throw new IllegalArgumentException("Invalid codex path: " + path);
        }
        return path;
    }

    static Path root(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve("codex");
    }

    static DynamicOps<JsonElement> ops(MinecraftServer server) {
        return RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
    }

    private Path resolve(Path root, String resourcePath) {
        var base = root.toAbsolutePath().normalize();
        var file = base.resolve(directory).resolve(relativePath(resourcePath) + ".json");
        for (var current = file; current != null && current.startsWith(base); current = current.getParent()) {
            if (Files.isSymbolicLink(current)) {
                throw new IllegalStateException("Symbolic links are not allowed in codex paths: " + current);
            }
        }
        return file;
    }

    <T> T load(Path root, String path, Codec<T> codec, Supplier<T> defaultValue, DynamicOps<JsonElement> ops) {
        var file = resolve(root, path);
        try (var reader = new JsonReader(Files.newBufferedReader(file, StandardCharsets.UTF_8))) {
            reader.setLenient(false);
            if (reader.peek() == JsonToken.END_DOCUMENT) throw new JsonSyntaxException("Empty JSON document");
            var json = Streams.parse(reader);
            if (reader.peek() != JsonToken.END_DOCUMENT) throw new JsonSyntaxException("Trailing JSON data");
            return codec.parse(ops, json).getOrThrow(message ->
                    new IllegalStateException("Failed to decode codex resource " + file + ": " + message));
        } catch (NoSuchFileException exception) {
            return defaultValue.get();
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load codex resource " + file, exception);
        } catch (JsonParseException exception) {
            throw new IllegalStateException("Malformed codex resource " + file, exception);
        }
    }

    <T> void save(Path root, String path, Codec<T> codec, T value, DynamicOps<JsonElement> ops) {
        var file = resolve(root, path);
        var json = codec.encodeStart(ops, value).getOrThrow(message ->
                new IllegalStateException("Failed to encode codex resource " + file + ": " + message));
        var contents = GSON.toJson(json);
        try {
            Files.createDirectories(file.getParent());
            var temporary = Files.createTempFile(file.getParent(), file.getFileName() + ".", ".tmp");
            try {
                Files.writeString(temporary, contents, StandardCharsets.UTF_8);
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to save codex resource " + file, exception);
        }
    }

    public static final class Builder {
        private final Codex codex;

        private Builder(Codex codex) {
            this.codex = codex;
        }

        public <T> CodexEntry<T> entry(CodexEntry.Builder<T> builder) {
            return builder.build(codex);
        }

        public <T> CodexArchive<T> archive(CodexArchive.Builder<T> builder) {
            return builder.build(codex);
        }

        public Codex build() {
            return codex;
        }
    }
}
*///? }
