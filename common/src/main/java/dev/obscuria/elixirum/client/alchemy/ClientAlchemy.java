package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.client.alchemy.cache.AlchemyCache;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public final class ClientAlchemy implements Alchemy {

    public static final ClientAlchemy INSTANCE = new ClientAlchemy();

    public final List<CachedElixir> recentlyBrewed = new ArrayList<>();

    private final ClientAlchemyEssences essences = new ClientAlchemyEssences();
    private final ClientAlchemyIngredients ingredients = new ClientAlchemyIngredients();
    private final ClientAlchemyProfile localProfile = new ClientAlchemyProfile();

    public static ClientAlchemyProfile localProfile() {
        return INSTANCE.localProfile;
    }

    public void justBrewed(AlchemyRecipe recipe) {

        var elixir = AlchemyCache.cachedElixirOf(recipe);
        recentlyBrewed.removeIf(cached -> cached.recipe().equals(recipe));
        recentlyBrewed.add(0, elixir);

        localProfile.mastery()._addRecipeXp(recipe.uuid(), 1);
        localProfile.knownIngredients().discoverAll(recipe);
        localProfile.knownRecipes().update(recipe.uuid(), elixir.contents());
    }

    @Override
    public ClientAlchemyEssences essences() {
        return essences;
    }

    @Override
    public ClientAlchemyIngredients ingredients() {
        return ingredients;
    }

    @Override
    public ClientAlchemyProfile profileOf(Player player) {
        return Minecraft.getInstance().player != player
                ? new ClientAlchemyProfile()
                : localProfile;
    }
}