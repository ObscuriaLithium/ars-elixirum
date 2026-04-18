package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.archivist.api.v1.codex.Codex;
import dev.obscuria.archivist.api.v1.codex.CodexArchive;
import dev.obscuria.archivist.api.v1.codex.CodexEntry;
import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileData;

public final class AlchemyCodex {

    public static final Codex CODEX;
    public static final CodexEntry<AlchemyEssencesData> ESSENCES;
    public static final CodexEntry<AlchemyIngredientsData> INGREDIENTS;
    public static final CodexArchive<AlchemyProfileData> PROFILES;

    public static void init() {}

    static {
        final var builder = Codex.createBuilder("obscuria/alchemy");
        ESSENCES = builder.entry(CodexEntry.<AlchemyEssencesData>createBuilder()
                .entryCodec(AlchemyEssencesData.CODEC)
                .defaultValue(AlchemyEssencesData::empty)
                .entryPath("essences"));
        INGREDIENTS = builder.entry(CodexEntry.<AlchemyIngredientsData>createBuilder()
                .entryCodec(AlchemyIngredientsData.CODEC)
                .defaultValue(AlchemyIngredientsData::empty)
                .entryPath("ingredients"));
        PROFILES = builder.archive(CodexArchive.<AlchemyProfileData>createBuilder()
                .entryCodec(AlchemyProfileData.CODEC)
                .defaultValue(AlchemyProfileData::empty)
                .archivePath("profiles"));
        CODEX = builder.build();
    }
}
