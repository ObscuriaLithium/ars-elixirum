package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.api.AlchemyEssencesView;
import dev.obscuria.elixirum.api.AlchemyIngredientsView;
import dev.obscuria.elixirum.client.alchemy.cache.AlchemyCache;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.alchemy.profiles.ClientAlchemyProfile;
import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileData;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileView;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ClientAlchemy implements Alchemy {

    public static final ClientAlchemy INSTANCE = new ClientAlchemy();

    public final List<CachedElixir> recentlyBrewed = new ArrayList<>();

    private final ClientAlchemyEssences essences = new ClientAlchemyEssences();
    private final ClientAlchemyIngredients ingredients = new ClientAlchemyIngredients();
    private final ClientAlchemyProfile profile = new ClientAlchemyProfile();

    public AlchemyProfileView localProfile() {
        return profile;
    }

    public void justBrewed(AlchemyRecipe recipe) {
        recentlyBrewed.removeIf(cached -> cached.recipe().isSame(recipe));
        recentlyBrewed.add(0, AlchemyCache.cachedElixirOf(recipe));
        localProfile().mastery().grandXp(recipe.uuid(), 1);
        localProfile().statistics().brewed(recipe);
    }

    public void updateEssences(AlchemyEssencesData data) {
        this.essences.update(data);
    }

    public void updateIngredients(AlchemyIngredientsData data) {
        this.ingredients.update(data);
    }

    public void updateProfile(AlchemyProfileData data) {
        this.profile.update(data);
    }

    @Override
    public AlchemyEssencesView essences() {
        return essences;
    }

    @Override
    public AlchemyIngredientsView ingredients() {
        return ingredients;
    }

    @Override
    public AlchemyProfileView profileOf(Player player) {
        return profile;
    }
}