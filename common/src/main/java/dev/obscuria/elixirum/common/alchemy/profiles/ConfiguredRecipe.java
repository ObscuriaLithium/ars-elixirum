package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public record ConfiguredRecipe(
        AlchemyRecipe recipe,
        AtomicReference<StyleVariant> style,
        AtomicReference<Chroma> chroma
) {

    public static final Codec<ConfiguredRecipe> CODEC;

    public static ConfiguredRecipe empty() {
        return new ConfiguredRecipe(AlchemyRecipe.EMPTY, StyleVariant.DEFAULT, Chroma.NATURAL);
    }

    public ConfiguredRecipe(AlchemyRecipe recipe) {
        this(recipe, StyleVariant.DEFAULT, Chroma.NATURAL);
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

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyRecipe.CODEC.fieldOf("recipe").forGetter(ConfiguredRecipe::recipe),
                StyleVariant.PACKED_CODEC.fieldOf("style").forGetter(ConfiguredRecipe::getStyle),
                Chroma.CODEC.fieldOf("chroma").forGetter(ConfiguredRecipe::getChroma)
        ).apply(codec, ConfiguredRecipe::new));
    }
}
