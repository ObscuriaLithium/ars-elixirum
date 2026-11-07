package dev.obscuria.elixirum.compat.archivist.codex;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.function.Supplier;

public final class CodexEntry<T> {

    private final Codex parent;
    private final Codec<T> codec;
    private final Supplier<T> defaultValue;
    private final String path;

    private CodexEntry(Codex parent, Codec<T> codec, Supplier<T> defaultValue, String path) {
        this.parent = parent;
        this.codec = Objects.requireNonNull(codec, "Entry codec must not be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default supplier must not be null");
        this.path = Codex.relativePath(path);
    }

    public static <T> Builder<T> createBuilder() {
        return new Builder<>();
    }

    public T load(MinecraftServer server) {
        return parent.load(Codex.root(server), path, codec, defaultValue, Codex.ops(server));
    }

    public void save(MinecraftServer server, T value) {
        parent.save(Codex.root(server), path, codec, value, Codex.ops(server));
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

        public Builder<T> entryPath(String path) {
            this.path = path;
            return this;
        }

        CodexEntry<T> build(Codex parent) {
            return new CodexEntry<>(parent, codec, defaultValue, path);
        }
    }
}
