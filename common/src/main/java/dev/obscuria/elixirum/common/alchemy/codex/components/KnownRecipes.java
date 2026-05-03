package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KnownRecipes {

    public static final Codec<KnownRecipes> CODEC;

    @Getter public final Map<UUID, Entry> entries;

    public static KnownRecipes empty() {
        return new KnownRecipes(Map.of());
    }

    public KnownRecipes(Map<UUID, Entry> entries) {
        this.entries = new HashMap<>(entries);
    }

    public int getBestQuality(UUID recipeUid) {
        var entry = this.entries.get(recipeUid);
        return entry == null ? 0 : entry.getBestQuality();
    }

    public int getBrewCount(UUID recipeUid) {
        var entry = this.entries.get(recipeUid);
        return entry == null ? 0 : entry.getBrewCount();
    }

    public void update(UUID recipeUid, ElixirContents contents) {
        this.updateBestQuality(recipeUid, (int) contents.quality());
        this.updateBrewCount(recipeUid);
    }

    public void updateBestQuality(UUID recipeUid, int quality) {
        var entry = this.getOrCreate(recipeUid);
        entry.bestQuality = Math.max(quality, entry.bestQuality);
    }

    public void updateBrewCount(UUID recipeUid) {
        this.getOrCreate(recipeUid).brewCount++;
    }

    private Entry getOrCreate(UUID recipeUid) {
        return this.entries.computeIfAbsent(recipeUid, Entry::new);
    }

    static {
        CODEC = Codec
                .unboundedMap(UUIDUtil.STRING_CODEC, Entry.CODEC)
                .xmap(KnownRecipes::new, KnownRecipes::getEntries);
    }

    public static class Entry {

        public static final Codec<Entry> CODEC;

        @Getter public int bestQuality;
        @Getter public int brewCount;
        @Getter public int fabledInstances;

        public Entry(int bestQuality, int brewCount, int fabledInstances) {
            this.bestQuality = bestQuality;
            this.brewCount = brewCount;
            this.fabledInstances = fabledInstances;
        }

        private Entry(UUID recipeUid) {
            this(0, 0, 0);
        }

        static {
            CODEC = RecordCodecBuilder.create(codec -> codec.group(
                    Codec.INT.optionalFieldOf("best_quality", 0).forGetter(Entry::getBestQuality),
                    Codec.INT.optionalFieldOf("brew_count", 0).forGetter(Entry::getBrewCount),
                    Codec.INT.optionalFieldOf("fabled_instances", 0).forGetter(Entry::getFabledInstances)
            ).apply(codec, Entry::new));
        }
    }
}
