package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyIngredients;

public final class ClientAlchemyIngredients extends AbstractAlchemyIngredients {

    ClientAlchemyIngredients() {}

    public void update(Diff generationResult) {
        this.generationResult = generationResult;
    }
}
