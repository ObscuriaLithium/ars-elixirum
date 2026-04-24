package dev.obscuria.elixirum.server.alchemy.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import dev.obscuria.elixirum.common.alchemy.recipes.RecipeKind;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record PredefinedRecipe(
        RecipeKind kind,
        AlchemyRecipe recipe,
        List<EffectProvider> effects,
        Optional<Component> displayName,
        Optional<RGB> color
) {

    public static final Codec<PredefinedRecipe> DIRECT_CODEC;

    public boolean isFor(AlchemyRecipe recipe) {
        return this.recipe.equals(recipe);
    }

    static {
        DIRECT_CODEC = RecordCodecBuilder.create(codec -> codec.group(
                RecipeKind.CODEC.fieldOf("kind").forGetter(PredefinedRecipe::kind),
                AlchemyRecipe.CODEC.fieldOf("recipeUid").forGetter(PredefinedRecipe::recipe),
                EffectProvider.CODEC.listOf().fieldOf("elixirContents").forGetter(PredefinedRecipe::effects),
                ExtraCodecs.FLAT_COMPONENT.optionalFieldOf("display_name").forGetter(PredefinedRecipe::displayName),
                RGB.CODEC.optionalFieldOf("iconColor").forGetter(PredefinedRecipe::color)
        ).apply(codec, PredefinedRecipe::new));
    }
}
