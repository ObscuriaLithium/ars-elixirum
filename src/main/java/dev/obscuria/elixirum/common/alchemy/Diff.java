package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Diff(
        Entry added,
        Entry removed,
        Entry updated
) {

    public static final Codec<Diff> CODEC;

    public static Diff empty() {
        return create(0, false, 0, false, 0, false);
    }

    public static Diff create(
            int addedCount, boolean addedApplied,
            int removedCount, boolean removedApplied,
            int updatedCount, boolean updatedApplied
    ) {
        return new Diff(
                new Entry(addedCount, addedApplied),
                new Entry(removedCount, removedApplied),
                new Entry(updatedCount, updatedApplied)
        );
    }

    public boolean requiresRegenerate() {
        return added.requiresManualAction()
                || removed.requiresManualAction()
                || updated.requiresManualAction();
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Entry.CODEC.fieldOf("added").forGetter(Diff::added),
                Entry.CODEC.fieldOf("removed").forGetter(Diff::removed),
                Entry.CODEC.fieldOf("updated").forGetter(Diff::updated)
        ).apply(codec, Diff::new));
    }

    public record Entry(
            int count,
            boolean applied
    ) {

        public static final Codec<Entry> CODEC;

        public boolean requiresManualAction() {
            return !applied && count > 0;
        }

        static {
            CODEC = RecordCodecBuilder.create(codec -> codec.group(
                    Codec.INT.fieldOf("count").forGetter(Entry::count),
                    Codec.BOOL.fieldOf("applied").forGetter(Entry::applied)
            ).apply(codec, Entry::new));
        }
    }
}