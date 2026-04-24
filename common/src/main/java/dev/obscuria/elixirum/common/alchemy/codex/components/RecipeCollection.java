package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.recipes.ConfiguredRecipe;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.alchemy.styles.StyleVariant;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;

import java.util.*;
import java.util.stream.Stream;

public final class RecipeCollection {

    public static final Codec<RecipeCollection> CODEC;

    @Getter(AccessLevel.PRIVATE) private final Map<UUID, ConfiguredRecipe> configByRecipe;
    @Getter(AccessLevel.PRIVATE) private final List<UUID> recipeOrder;

    public static RecipeCollection empty() {
        return new RecipeCollection(Map.of(), List.of());
    }

    public RecipeCollection(Map<UUID, ConfiguredRecipe> savedRecipes, List<UUID> recipeOrder) {
        this.configByRecipe = new HashMap<>(savedRecipes);
        this.recipeOrder = new ArrayList<>(recipeOrder);
        this.normalize();
    }

    public Stream<ConfiguredRecipe> streamOrdered() {
        return recipeOrder.stream().map(configByRecipe::get);
    }

    public boolean isSaved(UUID recipeUid) {
        return configByRecipe.containsKey(recipeUid);
    }

    public boolean isAbsent(UUID recipeUid) {
        return !configByRecipe.containsKey(recipeUid);
    }

    public boolean move(UUID recipeUid, int newIndex) {
        var oldIndex = recipeOrder.indexOf(recipeUid);
        if (oldIndex == -1) return false;
        this.recipeOrder.remove(oldIndex);
        this.recipeOrder.add(Mth.clamp(newIndex, 0, recipeOrder.size()), recipeUid);
        return true;
    }

    public boolean swap(UUID firstUid, UUID secondUid) {
        var firstIndex = recipeOrder.indexOf(firstUid);
        var secondIndex = recipeOrder.indexOf(secondUid);
        if (firstIndex == -1 || secondIndex == -1) return false;
        Collections.swap(recipeOrder, firstIndex, secondIndex);
        return true;
    }

    public Optional<ConfiguredRecipe> findConfig(UUID recipeUid) {
        return Optional.ofNullable(configByRecipe.get(recipeUid));
    }

    public boolean save(ConfiguredRecipe recipe) {
        if (configByRecipe.containsKey(recipe.recipe().getUuid())) return false;
        this.configByRecipe.put(recipe.recipe().getUuid(), recipe);
        this.recipeOrder.add(recipe.recipe().getUuid());
        return true;
    }

    public boolean remove(UUID recipeUid) {
        if (!configByRecipe.containsKey(recipeUid)) return false;
        this.configByRecipe.remove(recipeUid);
        this.recipeOrder.remove(recipeUid);
        return true;
    }

    public boolean setStyle(UUID recipeUid, StyleVariant style) {
        if (!configByRecipe.containsKey(recipeUid)) return false;
        this.configByRecipe.get(recipeUid).setStyle(style);
        return true;
    }

    public boolean setChroma(UUID recipeUid, Chroma chroma) {
        if (!configByRecipe.containsKey(recipeUid)) return false;
        this.configByRecipe.get(recipeUid).setChroma(chroma);
        return true;
    }

    private void normalize() {
        this.recipeOrder.removeIf(this::isAbsent);
        var present = new HashSet<>(recipeOrder);
        for (var id : configByRecipe.keySet()) {
            if (!present.contains(id)) {
                this.recipeOrder.add(id);
            }
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, ConfiguredRecipe.CODEC).fieldOf("config_by_recipe").forGetter(RecipeCollection::getConfigByRecipe),
                UUIDUtil.STRING_CODEC.listOf().fieldOf("recipe_order").forGetter(RecipeCollection::getRecipeOrder)
        ).apply(codec, RecipeCollection::new));
    }
}
