package dev.obscuria.elixirum.common.events;

import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.api.events.MasteryListener;
import dev.obscuria.elixirum.common.alchemy.codex.components.AlchemyMastery;
import dev.obscuria.elixirum.common.network.ClientboundMasteryLevelUpPayload;
import dev.obscuria.elixirum.common.network.ClientboundMasterySyncPayload;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemyProfile;

public final class _CommonMasteryListener implements MasteryListener {

    @Override
    public void onXpGrant(AlchemyProfile profile, AlchemyMastery mastery, int amount) {
        if (!(profile instanceof ServerAlchemyProfile serverProfile)) return;
        serverProfile.sendToClient(new ClientboundMasterySyncPayload(mastery));
    }

    @Override
    public void onLevelSet(AlchemyProfile profile, AlchemyMastery mastery) {
        if (!(profile instanceof ServerAlchemyProfile serverProfile)) return;
        serverProfile.sendToClient(new ClientboundMasterySyncPayload(mastery));
    }

    @Override
    public void onLevelUp(AlchemyProfile profile, AlchemyMastery mastery) {
        if (!(profile instanceof ServerAlchemyProfile serverProfile)) return;
        serverProfile.sendToClient(new ClientboundMasteryLevelUpPayload(mastery.getLevel()));
    }
}
