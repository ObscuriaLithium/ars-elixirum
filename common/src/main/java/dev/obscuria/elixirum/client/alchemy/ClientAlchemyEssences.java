package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyEssences;

public final class ClientAlchemyEssences extends AbstractAlchemyEssences {

    ClientAlchemyEssences() {}

    public void update(Diff generationResult) {
        this.generationResult = generationResult;
    }
}