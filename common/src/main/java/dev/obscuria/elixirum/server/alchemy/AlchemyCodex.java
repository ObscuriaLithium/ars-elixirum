package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.archivist.api.v1.codex.Codex;
import dev.obscuria.archivist.api.v1.codex.CodexArchive;
import dev.obscuria.archivist.api.v1.codex.CodexEntry;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyEssences;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyIngredients;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyProfile;

public final class AlchemyCodex {

    public static final Codex CODEX;
    public static final CodexEntry<PackedAlchemyEssences> ESSENCES;
    public static final CodexEntry<PackedAlchemyIngredients> INGREDIENTS;
    public static final CodexArchive<PackedAlchemyProfile> PROFILES;

    public static void init() {}

    static {
        final var builder = Codex.createBuilder("obscuria/alchemy");
        ESSENCES = builder.entry(CodexEntry.<PackedAlchemyEssences>createBuilder()
                .entryCodec(PackedAlchemyEssences.CODEC)
                .defaultValue(() -> PackedAlchemyEssences.EMPTY)
                .entryPath("essences"));
        INGREDIENTS = builder.entry(CodexEntry.<PackedAlchemyIngredients>createBuilder()
                .entryCodec(PackedAlchemyIngredients.CODEC)
                .defaultValue(() -> PackedAlchemyIngredients.EMPTY)
                .entryPath("ingredients"));
        PROFILES = builder.archive(CodexArchive.<PackedAlchemyProfile>createBuilder()
                .entryCodec(PackedAlchemyProfile.CODEC)
                .defaultValue(() -> PackedAlchemyProfile.EMPTY)
                .archivePath("profiles"));
        CODEX = builder.build();
    }
}
