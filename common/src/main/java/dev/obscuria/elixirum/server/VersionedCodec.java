package dev.obscuria.elixirum.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.List;

public final class VersionedCodec {

    public static <A> Builder<A> of(Codec<A> currentCodec) {
        return new Builder<>(currentCodec);
    }

    public static final class Builder<A> {

        private final Codec<A> codec;
        private final List<Migration> migrations = new ArrayList<>();

        private Builder(Codec<A> codec) {
            this.codec = codec;
        }

        public Builder<A> migrate(Migration migration) {
            this.migrations.add(migration);
            return this;
        }

        public Codec<A> build() {
            final var frozenMigrations = List.copyOf(migrations);
            final var currentVersion = frozenMigrations.size();
            final var innerCodec = codec;

            return new Codec<>() {

                @Override
                public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                    var data = new Dynamic<>(ops, input);
                    var version = data.get("version").asInt(0);
                    for (var i = version; i < frozenMigrations.size(); i++) {
                        data = frozenMigrations.get(i).apply(data);
                    }
                    return innerCodec.decode(ops, data.getValue());
                }

                @Override
                public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                    return innerCodec.encode(input, ops, prefix).flatMap(encoded -> {
                        var data = new Dynamic<>(ops, encoded);
                        data = data.set("version", data.createInt(currentVersion));
                        return DataResult.success(data.getValue());
                    });
                }
            };
        }
    }

    @FunctionalInterface
    public interface Migration {

        <T> Dynamic<T> apply(Dynamic<T> data);
    }

    public static Migration renameField(String oldName, String newName) {

        return new Migration() {
            @Override
            public <T> Dynamic<T> apply(Dynamic<T> data) {
                var value = data.get(oldName).result();
                if (value.isEmpty()) return data;
                return data.set(newName, value.orElseThrow()).remove(oldName);
            }
        };
    }

    public static Migration addIntField(String name, int defaultValue) {

        return new Migration() {
            @Override
            public <T> Dynamic<T> apply(Dynamic<T> data) {
                var present = data.get(name).asNumber().result().isPresent();
                if (present) return data;
                return data.set(name, data.createInt(defaultValue));
            }
        };
    }

    public static Migration addStringField(String name, String defaultValue) {

        return new Migration() {
            @Override
            public <T> Dynamic<T> apply(Dynamic<T> data) {
                var present = data.get(name).asString().result().isPresent();
                if (present) return data;
                return data.set(name, data.createString(defaultValue));
            }
        };
    }

    public static Migration removeField(String name) {

        return new Migration() {
            @Override
            public <T> Dynamic<T> apply(Dynamic<T> data) {
                return data.remove(name);
            }
        };
    }
}