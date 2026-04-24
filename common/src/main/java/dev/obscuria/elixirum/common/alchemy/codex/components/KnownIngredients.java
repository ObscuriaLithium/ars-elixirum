package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import lombok.Getter;
import net.minecraft.world.item.Item;

import java.util.*;

public final class KnownIngredients {

    public static final Codec<KnownIngredients> CODEC;

    @Getter public final Map<Item, Entry> entries;

    public static KnownIngredients empty() {
        return new KnownIngredients(Map.of());
    }

    public KnownIngredients(Map<Item, Entry> entries) {
        this.entries = new HashMap<>(entries);
    }

    public void discoverAll(AlchemyRecipe recipe) {
        recipe.getBase().map(AlchemyIngredient::asItem).ifPresent(this::discoverAsBase);
        recipe.getCatalyst().map(AlchemyIngredient::asItem).ifPresent(this::discoverAsCatalyst);
        recipe.getInhibitor().map(AlchemyIngredient::asItem).ifPresent(this::discoverAsInhibitor);
    }

    public void discoverAsBase(Item item) {
        getOrCreate(item).knownAsBase = true;
    }

    public void discoverAsCatalyst(Item item) {
        getOrCreate(item).knownAsCatalyst = true;
    }

    public void discoverAsInhibitor(Item item) {
        getOrCreate(item).knownAsInhibitor = true;
    }

    private Entry getOrCreate(Item item) {
        return entries.computeIfAbsent(item, Entry::new);
    }

    static {
        CODEC = Codec
                .unboundedMap(CodecHelper.STRICT_ITEM, Entry.CODEC)
                .xmap(KnownIngredients::new, KnownIngredients::getEntries);
    }

    public static class Entry {

        public static final Codec<Entry> CODEC;

        @Getter public Set<EssenceHolder> knownEssences;
        @Getter public boolean knownAsBase;
        @Getter public boolean knownAsCatalyst;
        @Getter public boolean knownAsInhibitor;

        public static Entry empty() {
            return new Entry(Set.of(), false, false, false);
        }

        public Entry(
                Set<EssenceHolder> knownEssences,
                boolean knownAsBase,
                boolean knownAsCatalyst,
                boolean knownAsInhibitor
        ) {
            this.knownEssences = new HashSet<>(knownEssences);
            this.knownAsBase = knownAsBase;
            this.knownAsCatalyst = knownAsCatalyst;
            this.knownAsInhibitor = knownAsInhibitor;
        }

        private Entry(
                List<EssenceHolder> knownEssences,
                boolean knownAsBase,
                boolean knownAsCatalyst,
                boolean knownAsInhibitor
        ) {
            this(new HashSet<>(knownEssences), knownAsBase, knownAsCatalyst, knownAsInhibitor);
        }

        private Entry(Item item) {
            this(Set.of(), false, false, false);
        }

        private List<EssenceHolder> knownEssenceList() {
            return List.copyOf(knownEssences);
        }

        static {
            CODEC = RecordCodecBuilder.create(codec -> codec.group(
                    EssenceHolder.CODEC.listOf().optionalFieldOf("known_essences", List.of()).forGetter(Entry::knownEssenceList),
                    Codec.BOOL.optionalFieldOf("known_as_1", false).forGetter(Entry::isKnownAsBase),
                    Codec.BOOL.optionalFieldOf("known_as_2", false).forGetter(Entry::isKnownAsCatalyst),
                    Codec.BOOL.optionalFieldOf("known_as_3", false).forGetter(Entry::isKnownAsInhibitor)
            ).apply(codec, Entry::new));
        }
    }
}
