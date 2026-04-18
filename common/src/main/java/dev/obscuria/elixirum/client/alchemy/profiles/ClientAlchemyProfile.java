package dev.obscuria.elixirum.client.alchemy.profiles;

import dev.obscuria.elixirum.common.alchemy.profiles.*;

public final class ClientAlchemyProfile implements AlchemyProfileView {

    private AlchemyProfileData data = AlchemyProfileData.empty();

    public void update(AlchemyProfileData data) {
        this.data = data;
    }

    @Override
    public ProfileMastery mastery() {
        return data.mastery();
    }

    @Override
    public ProfileKnowledge knowledge() {
        return data.knowledge();
    }

    @Override
    public ProfileCollection collection() {
        return data.collection();
    }

    @Override
    public ProfileStatistics statistics() {
        return data.statistics();
    }
}