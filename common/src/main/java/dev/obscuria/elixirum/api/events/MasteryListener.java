package dev.obscuria.elixirum.api.events;

import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.common.alchemy.codex.components.AlchemyMastery;

import java.util.UUID;

public interface MasteryListener {

    default void onRecipeXpGrant(AlchemyProfile profile, AlchemyMastery mastery, UUID recipeUid, int amount) {}

    default void onXpGrant(AlchemyProfile profile, AlchemyMastery mastery, int amount) {}

    default void onLevelSet(AlchemyProfile profile, AlchemyMastery mastery) {}

    default void onLevelUp(AlchemyProfile profile, AlchemyMastery mastery) {}
}
