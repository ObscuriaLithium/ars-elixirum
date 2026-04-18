package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ProfileCollection(
        List<ConfiguredRecipe> recipes
) {

    public static final Codec<ProfileCollection> CODEC;

    public static ProfileCollection empty() {
        return new ProfileCollection(new ArrayList<>());
    }

    public boolean hasRecipe(AlchemyRecipe recipe) {
        for (var configured : recipes) {
            if (!configured.recipe().isSame(recipe)) continue;
            return true;
        }
        return false;
    }

    public boolean saveRecipe(ConfiguredRecipe recipe) {
        if (recipes.contains(recipe)) return false;
        this.recipes.add(recipe);
        return true;
    }

    public boolean removeRecipe(ConfiguredRecipe recipe) {
        return recipes.remove(recipe);
    }

    public Optional<ConfiguredRecipe> findConfig(UUID recipe) {
        for (var configured : recipes) {
            if (!configured.recipe().isSame(recipe)) continue;
            return Optional.of(configured);
        }
        return Optional.empty();
    }

    private static ProfileCollection fromCodec(List<ConfiguredRecipe> recipes) {
        return new ProfileCollection(new ArrayList<>(recipes));
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ConfiguredRecipe.CODEC.listOf().fieldOf("recipes").forGetter(ProfileCollection::recipes)
        ).apply(codec, ProfileCollection::fromCodec));
    }
}
