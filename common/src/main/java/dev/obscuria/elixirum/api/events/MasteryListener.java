package dev.obscuria.elixirum.api.events;

import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.api.codex.profile.AlchemyMastery;

import java.util.UUID;

public interface MasteryListener {

    default void onXpChanged(AlchemyProfile profile, AlchemyMastery mastery) {}

    default void onRecipeXpChanged(AlchemyProfile profile, AlchemyMastery mastery, UUID recipeUid) {}

    default void onLevelChanged(AlchemyProfile profile, AlchemyMastery mastery) {}

    default void onLevelUp(AlchemyProfile profile, AlchemyMastery mastery) {}
}
