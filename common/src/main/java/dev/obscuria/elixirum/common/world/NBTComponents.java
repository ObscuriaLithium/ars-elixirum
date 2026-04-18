package dev.obscuria.elixirum.common.world;

import dev.obscuria.archivist.api.v1.nbt.NBTComponent;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;

public final class NBTComponents {

    public static final NBTComponent<ElixirContents> ELIXIR_CONTENTS;
    public static final NBTComponent<ExtractContents> EXTRACT_CONTENTS;
    public static final NBTComponent<StyleVariant> STYLE;
    public static final NBTComponent<Chroma> CHROMA;

    static {
        ELIXIR_CONTENTS = NBTComponent.<ElixirContents>createBuilder()
                .path("ArsElixirum.Contents")
                .codec(ElixirContents.CODEC)
                .build();
        EXTRACT_CONTENTS = NBTComponent.<ExtractContents>createBuilder()
                .path("ArsElixirum.ExtractContents")
                .codec(ExtractContents.CODEC)
                .build();
        STYLE = NBTComponent.<StyleVariant>createBuilder()
                .path("ArsElixirum.Style")
                .codec(StyleVariant.CODEC)
                .build();
        CHROMA = NBTComponent.<Chroma>createBuilder()
                .path("ArsElixirum.Chroma")
                .codec(Chroma.CODEC)
                .build();
    }
}
