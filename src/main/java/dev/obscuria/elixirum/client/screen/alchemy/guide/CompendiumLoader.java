package dev.obscuria.elixirum.client.screen.alchemy.guide;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class CompendiumLoader {

    public static DataResult<Compendium> load() {
        var manager = Minecraft.getInstance().getResourceManager();
        var locale = Minecraft.getInstance().options.languageCode;
        if (locale.equals("en_us")) return load("en_us", manager);
        var result = load(locale, manager);
        return result.error().isPresent() ? load("en_us", manager) : result;
    }

    private static DataResult<Compendium> load(String locale, ResourceManager manager) {
        return loadElement(manager, Compendium.Index.CODEC, resolveIndexId(locale))
                .mapError("Failed to load index: %s"::formatted)
                .flatMap(index -> loadPages(locale, manager, index)
                        .map(pages -> new Compendium(index, pages)));
    }

    @SuppressWarnings("ConstantConditions")
    private static DataResult<Map<String, Compendium.Page>> loadPages(String locale, ResourceManager manager, Compendium.Index index) {
        var pages = new HashMap<String, Compendium.Page>();

        var homepage = loadElement(manager, Compendium.Page.CODEC, resolvePageId(locale, index.homepage()));
        if (homepage.error().isPresent()) return homepage
                .mapError("Failed to load homepage: %s"::formatted)
                .map(ignored -> null);
        homepage.result().ifPresent(page -> pages.put(index.homepage(), page));

        for (var section : index.sections()) {
            for (var pageKey : section.pages()) {
                var result = loadElement(manager, Compendium.Page.CODEC, resolvePageId(locale, pageKey));
                if (result.error().isPresent()) return result
                        .mapError(msg -> "Failed to load page '%s': %s".formatted(pageKey, msg))
                        .map(ignored -> null);
                result.result().ifPresent(page -> pages.put(pageKey, page));
            }
        }

        return DataResult.success(Map.copyOf(pages));
    }

    private static <T> DataResult<T> loadElement(ResourceManager manager, Codec<T> codec, ResourceLocation id) {
        @Nullable var resource = manager.getResource(id).orElse(null);
        if (resource == null) return DataResult.error(() -> "File not found: " + id);

        JsonElement json;
        try (var reader = resource.openAsReader()) {
            json = JsonParser.parseReader(reader);
        } catch (IOException exception) {
            return DataResult.error(() -> "Failed to open file: " + exception.getMessage());
        } catch (JsonParseException exception) {
            return DataResult.error(() -> "Invalid JSON: " + exception.getMessage());
        }

        return codec.decode(JsonOps.INSTANCE, json)
                .mapError(msg -> "Decoding error: " + msg)
                .map(Pair::getFirst);
    }

    private static ResourceLocation resolveIndexId(String locale) {
        return ArsElixirum.identifier("compendium/%s/index.json".formatted(locale));
    }

    private static ResourceLocation resolvePageId(String locale, String key) {
        return ArsElixirum.identifier("compendium/%s/%s".formatted(locale, key));
    }
}
