package dev.obscuria.elixirum.compat.archivist.codex;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.function.Supplier;

public final class CodexArchive<T> {

    private final Codex parent;
    private final Codec<T> codec;
    private final Supplier<T> defaultValue;
    private final String path;

    private CodexArchive(Codex parent, Codec<T> codec, Supplier<T> defaultValue, String path) {
        this.parent = parent;
        this.codec = Objects.requireNonNull(codec, "Entry codec must not be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default supplier must not be null");
        this.path = Codex.relativePath(path);
    }

    public static <T> Builder<T> createBuilder() {
        return new Builder<>();
    }

    private String entryPath(String name) {
        if (name == null || !name.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new IllegalArgumentException("Archive entry must be a UUID: " + name);
        }
        return path + "/" + name;
    }

    public T loadEntry(MinecraftServer server, String name) {
        var entry = entryPath(name);
        return parent.load(Codex.root(server), entry, codec, defaultValue, Codex.ops(server));
    }

    public void saveEntry(MinecraftServer server, String name, T value) {
        var entry = entryPath(name);
        parent.save(Codex.root(server), entry, codec, value, Codex.ops(server));
    }

    public static final class Builder<T> {
        private Codec<T> codec;
        private Supplier<T> defaultValue;
        private String path;

        private Builder() {}

        public Builder<T> entryCodec(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> archivePath(String path) {
            this.path = path;
            return this;
        }

        CodexArchive<T> build(Codex parent) {
            return new CodexArchive<>(parent, codec, defaultValue, path);
        }
    }
}
