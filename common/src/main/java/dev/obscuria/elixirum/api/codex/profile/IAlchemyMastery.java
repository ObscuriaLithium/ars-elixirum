package dev.obscuria.elixirum.api.codex.profile;

import java.util.UUID;

public interface IAlchemyMastery {

    int level();

    int xp();

    int xpForNextLevel();

    int recipeXp(UUID recipeUid);

    double progress();
}
