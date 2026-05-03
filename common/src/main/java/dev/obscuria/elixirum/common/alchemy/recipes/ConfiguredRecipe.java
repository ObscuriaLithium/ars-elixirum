package dev.obscuria.elixirum.common.alchemy.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public record ConfiguredRecipe(
        AlchemyRecipe recipe,
        AtomicReference<StyleVariant> style,
        AtomicReference<Chroma> chroma
) {

    public static final Codec<ConfiguredRecipe> CODEC;

    public static ConfiguredRecipe empty() {
        return new ConfiguredRecipe(AlchemyRecipe.empty(), StyleVariant.defaultVariant(), Chroma.NATURAL);
    }

    public ConfiguredRecipe(AlchemyRecipe recipe) {
        this(recipe, StyleVariant.defaultVariant(), Chroma.NATURAL);
    }

    public ConfiguredRecipe(AlchemyRecipe recipe, StyleVariant style, Chroma chroma) {
        this(recipe, new AtomicReference<>(style), new AtomicReference<>(chroma));
    }

    public StyleVariant getStyle() {
        return style.get();
    }

    public Chroma getChroma() {
        return chroma.get();
    }

    public void setStyle(StyleVariant style) {
        this.style.set(style);
    }

    public void mapStyle(Function<StyleVariant, StyleVariant> mapper) {
        this.setStyle(mapper.apply(getStyle()));
    }

    public void setChroma(Chroma chroma) {
        this.chroma.set(chroma);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyRecipe.codec().fieldOf("recipe").forGetter(ConfiguredRecipe::recipe),
                StyleVariant.packedCodec().optionalFieldOf("style", StyleVariant.defaultVariant()).forGetter(ConfiguredRecipe::getStyle),
                Chroma.CODEC.optionalFieldOf("chroma", Chroma.NATURAL).forGetter(ConfiguredRecipe::getChroma)
        ).apply(codec, ConfiguredRecipe::new));
    }
}
