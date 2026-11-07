package dev.obscuria.elixirum.compat.archivist.codex;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.compat.archivist.components.ComponentHolder;
import dev.obscuria.elixirum.compat.archivist.components.ComponentKey;
import dev.obscuria.elixirum.compat.archivist.components.ComponentMap;
import dev.obscuria.elixirum.compat.archivist.nbt.NBTComponent;
import dev.obscuria.elixirum.compat.BlockInteractions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ArchivistSelfCheck {

    public static void main(String[] args) throws IOException {
        if (!ArchivistSelfCheck.class.desiredAssertionStatus()) {
            throw new IllegalStateException("Run this self-check with -ea");
        }
        components();
        nbt();
        for (var result : InteractionResult.values()) {
            var mapped = BlockInteractions.itemResult(result);
            assert mapped.consumesAction() == result.consumesAction();
            assert mapped.result() == (result == InteractionResult.SUCCESS_NO_ITEM_USED ? InteractionResult.SUCCESS : result);
        }
        assert BlockInteractions.itemResult(InteractionResult.PASS) == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var root = Files.createTempDirectory("elixirum-archivist-");
        try {
            persistence(root);
        } finally {
            try (var files = Files.walk(root)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) Files.delete(file);
            }
        }
        System.out.println("Archivist compatibility self-check passed");
    }

    private static void components() {
        var number = ComponentKey.create(Codec.INT);
        var otherNumber = ComponentKey.create(Codec.INT);
        var text = ComponentKey.create(Codec.STRING);
        var byName = Map.<String, ComponentKey<?>>of("number", number, "other_number", otherNumber, "text", text);
        var byKey = new IdentityHashMap<ComponentKey<?>, String>();
        byName.forEach((name, key) -> byKey.put(key, name));
        Codec<ComponentKey<?>> keyCodec = Codec.STRING.flatXmap(
                name -> byName.containsKey(name) ? DataResult.success(byName.get(name)) : DataResult.error(() -> "Unknown key"),
                key -> byKey.containsKey(key) ? DataResult.success(byKey.get(key)) : DataResult.error(() -> "Unknown key"));
        var codec = ComponentKey.createValueMapCodec(keyCodec);
        var map = ComponentMap.empty();
        ComponentHolder holder = () -> map;
        assert holder.getOrCreate(number, () -> 7) == 7;
        assert holder.getOrCreate(otherNumber, () -> 9) == 9;
        assert holder.getOrCreate(text, () -> "value").equals("value");
        assert holder.getOrCreate(number, () -> { throw new AssertionError("Existing value replaced"); }) == 7;
        var encoded = codec.encodeStart(JsonOps.INSTANCE, map.asMap()).getOrThrow();
        assert encoded.equals(JsonParser.parseString("{\"number\":7,\"other_number\":9,\"text\":\"value\"}"));
        var decoded = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow();
        map.clear();
        map.putAll(decoded);
        assert map.getOrCreate(number, () -> 0) == 7;
        assert map.getOrCreate(otherNumber, () -> 0) == 9;
        assert map.getOrCreate(text, () -> "missing").equals("value");
        var copy = map.asMap();
        copy.clear();
        assert map.asMap().size() == 3;
        assert codec.encodeStart(JsonOps.INSTANCE, map.asMap()).getOrThrow().equals(encoded);
    }

    private static void nbt() {
        var component = NBTComponent.<Integer>createBuilder().path("ArsElixirum.Contents").codec(Codec.intRange(0, 10)).build();
        var root = new CompoundTag();
        root.putString("unrelated", "kept");
        assert component.read(root).isEmpty();
        assert component.readOrDefault(root, 3) == 3;
        component.write(root, 7);
        assert root.getCompound("ArsElixirum").getInt("Contents") == 7;
        assert component.read(root).orElseThrow() == 7;
        assert root.getString("unrelated").equals("kept");
        var saved = root.copy();
        component.write(root, 20);
        assert root.equals(saved) : "Failed encoding changed NBT";
        root.putString("ArsElixirum", "wrong type");
        saved = root.copy();
        component.write(root, 4);
        assert root.equals(saved) : "Non-compound parent was replaced";
        assert component.read(root).isEmpty();
        NBTComponent.<Integer>createBuilder().path("ArsElixirum.Contents").codec(Codec.INT.xmap(value -> value,
                value -> { throw new AssertionError("Wrong-type parent must abort before encoding"); })).build().write(root, 4);
        assert component.read((CompoundTag) null).isEmpty();
        fails(() -> NBTComponent.<Integer>createBuilder().path("ArsElixirum..Contents").codec(Codec.INT).build());
    }

    private static void persistence(Path root) throws IOException {
        var builder = Codex.createBuilder("obscuria/alchemy");
        builder.entry(CodexEntry.<Integer>createBuilder().entryCodec(Codec.INT).defaultValue(() -> 0).entryPath("essences"));
        var archive = builder.archive(CodexArchive.<Integer>createBuilder().entryCodec(Codec.INT).defaultValue(() -> 0).archivePath("profiles"));
        var codex = builder.build();
        var codec = Codec.intRange(0, 10);
        assert codex.load(root, "essences", codec, () -> 3, JsonOps.INSTANCE) == 3;
        var file = root.resolve("obscuria/alchemy/essences.json");
        assert Files.notExists(file);
        codex.save(root, "essences", codec, 7, JsonOps.INSTANCE);
        assert codex.load(root, "essences", codec, () -> 0, JsonOps.INSTANCE) == 7;
        var saved = Files.readString(file);
        assert saved.equals("7") : "Unexpected persistence envelope";
        codex.save(root, "essences", codec, 8, JsonOps.INSTANCE);
        assert codex.load(root, "essences", codec, () -> 0, JsonOps.INSTANCE) == 8;
        saved = Files.readString(file);
        fails(() -> codex.save(root, "essences", codec, 20, JsonOps.INSTANCE));
        assert Files.readString(file).equals(saved) : "Failed encoding overwrote saved data";
        for (var malformed : new String[]{"", "{", "7 8", "\"wrong type\""}) {
            Files.writeString(file, malformed);
            fails(() -> codex.load(root, "essences", codec, () -> 0, JsonOps.INSTANCE));
            assert Files.readString(file).equals(malformed) : "Failed load rewrote data";
        }
        var partial = "{\"good\":1,\"bad\":\"invalid\"}";
        Files.writeString(file, partial);
        fails(() -> codex.load(root, "essences", Codec.unboundedMap(Codec.STRING, Codec.INT), Map::of, JsonOps.INSTANCE));
        assert Files.readString(file).equals(partial) : "Partial decoding rewrote data";
        for (var malformed : new String[]{"{'good':1}", "{good:1}", "{\"good\":1,}"}) {
            Files.writeString(file, malformed);
            fails(() -> codex.load(root, "essences", Codec.unboundedMap(Codec.STRING, Codec.INT), Map::of, JsonOps.INSTANCE));
            assert Files.readString(file).equals(malformed);
        }
        var uuid = "01234567-89ab-cdef-0123-456789abcdef";
        codex.save(root, "profiles/" + uuid, Codec.INT, 9, JsonOps.INSTANCE);
        assert codex.load(root, "profiles/" + uuid, Codec.INT, () -> 0, JsonOps.INSTANCE) == 9;
        assert Files.exists(root.resolve("obscuria/alchemy/profiles/" + uuid + ".json"));
        fails(() -> archive.loadEntry(null, "../escape"));
        fails(() -> archive.saveEntry(null, "not-a-uuid", 1));
        fails(() -> Codex.createBuilder("../escape"));
        fails(() -> codex.save(root, "../escape", Codec.INT, 1, JsonOps.INSTANCE));
        fails(() -> codex.load(root, "/absolute", Codec.INT, () -> 0, JsonOps.INSTANCE));
        var blocked = root.resolve("obscuria/alchemy/blocked.json");
        Files.createDirectories(blocked);
        Files.writeString(blocked.resolve("keep"), "original");
        fails(() -> codex.save(root, "blocked", Codec.INT, 1, JsonOps.INSTANCE));
        assert Files.readString(blocked.resolve("keep")).equals("original");
        try (var files = Files.walk(root)) {
            assert files.noneMatch(path -> path.getFileName().toString().endsWith(".tmp")) : "Temporary save file leaked";
        }
    }

    private static void fails(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException | UncheckedIOException expected) {
            return;
        }
        throw new AssertionError("Expected failure");
    }
}
