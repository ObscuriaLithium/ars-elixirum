package dev.obscuria.elixirum.common.alchemy.codex;

import dev.obscuria.elixirum.api.codex.AlchemyProfile;

public abstract class AbstractAlchemyProfile implements AlchemyProfile {

    public void unpack(PackedAlchemyProfile packed) {
        this.getComponents().clear();
        this.getComponents().putAll(packed.components());
    }

    public PackedAlchemyProfile pack() {
        return new PackedAlchemyProfile(getComponents().asMap());
    }
}
