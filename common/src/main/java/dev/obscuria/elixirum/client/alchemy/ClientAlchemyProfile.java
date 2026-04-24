package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.archivist.api.v1.components.ComponentMap;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyProfile;
import lombok.Getter;

public final class ClientAlchemyProfile extends AbstractAlchemyProfile {

    @Getter private final ComponentMap components = ComponentMap.empty();
}