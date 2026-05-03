package dev.obscuria.elixirum.common.world;

import dev.obscuria.archivist.api.v1.nbt.NBTComponent;
import dev.obscuria.elixirum.api.alchemy.components.CustomText;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.alchemy.components.ExtractContents;
import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.server.alchemy.brewing.IngredientMixer;

public final class NBTComponents {

    public static final NBTComponent<IngredientMixer> MIXER;
    public static final NBTComponent<ElixirContents> ELIXIR_CONTENTS;
    public static final NBTComponent<ExtractContents> EXTRACT_CONTENTS;
    public static final NBTComponent<StyleVariant> STYLE;
    public static final NBTComponent<Chroma> CHROMA;
    public static final NBTComponent<CustomText> CUSTOM_NAME;
    public static final NBTComponent<CustomText> CUSTOM_LORE;

    static {
        MIXER = NBTComponent.<IngredientMixer>createBuilder()
                .path("ArsElixirum.Mixer")
                .codec(IngredientMixer.CODEC)
                .build();
        ELIXIR_CONTENTS = NBTComponent.<ElixirContents>createBuilder()
                .path("ArsElixirum.Contents")
                .codec(ElixirContents.codec())
                .build();
        EXTRACT_CONTENTS = NBTComponent.<ExtractContents>createBuilder()
                .path("ArsElixirum.ExtractContents")
                .codec(ExtractContents.codec())
                .build();
        STYLE = NBTComponent.<StyleVariant>createBuilder()
                .path("ArsElixirum.Style")
                .codec(StyleVariant.codec())
                .build();
        CHROMA = NBTComponent.<Chroma>createBuilder()
                .path("ArsElixirum.Chroma")
                .codec(Chroma.CODEC)
                .build();
        CUSTOM_NAME = NBTComponent.<CustomText>createBuilder()
                .path("ArsElixirum.CustomName")
                .codec(CustomText.codec())
                .build();
        CUSTOM_LORE = NBTComponent.<CustomText>createBuilder()
                .path("ArsElixirum.CustomLore")
                .codec(CustomText.codec())
                .build();
    }
}
