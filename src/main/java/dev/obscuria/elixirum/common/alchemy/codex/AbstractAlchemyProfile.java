package dev.obscuria.elixirum.common.alchemy.codex;

import dev.obscuria.archivist.api.v1.components.ComponentMap;
import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import lombok.Getter;

public abstract class AbstractAlchemyProfile implements AlchemyProfile {

    @Getter private final ComponentMap components = ComponentMap.empty();

    public void unpack(PackedAlchemyProfile packed) {
        this.getComponents().clear();
        this.getComponents().putAll(packed.components());
    }

    public PackedAlchemyProfile pack() {
        return new PackedAlchemyProfile(getComponents().asMap());
    }
}
