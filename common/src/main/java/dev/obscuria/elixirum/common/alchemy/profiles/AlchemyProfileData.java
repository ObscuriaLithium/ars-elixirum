package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record AlchemyProfileData(
        ProfileMastery mastery,
        ProfileKnowledge knowledge,
        ProfileCollection collection,
        ProfileStatistics statistics
) {

    public static final Codec<AlchemyProfileData> CODEC;

    private Optional<ProfileMastery> optionalMastery() {
        return Optional.of(mastery);
    }

    private Optional<ProfileKnowledge> optionalKnowledge() {
        return Optional.of(knowledge);
    }

    private Optional<ProfileCollection> optionalCollection() {
        return Optional.of(collection);
    }

    private Optional<ProfileStatistics> optionalStatistics() {
        return Optional.of(statistics);
    }

    public static AlchemyProfileData empty() {
        return new AlchemyProfileData(
                ProfileMastery.empty(),
                ProfileKnowledge.empty(),
                ProfileCollection.empty(),
                ProfileStatistics.empty());
    }

    @SuppressWarnings("all")
    private static AlchemyProfileData fromCodec(
            Optional<ProfileMastery> mastery,
            Optional<ProfileKnowledge> knowledge,
            Optional<ProfileCollection> collection,
            Optional<ProfileStatistics> statistics
    ) {
        return new AlchemyProfileData(
                mastery.orElseGet(ProfileMastery::empty),
                knowledge.orElseGet(ProfileKnowledge::empty),
                collection.orElseGet(ProfileCollection::empty),
                statistics.orElseGet(ProfileStatistics::empty));
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ProfileMastery.CODEC.optionalFieldOf("mastery").forGetter(AlchemyProfileData::optionalMastery),
                ProfileKnowledge.CODEC.optionalFieldOf("knowledge").forGetter(AlchemyProfileData::optionalKnowledge),
                ProfileCollection.CODEC.optionalFieldOf("collection").forGetter(AlchemyProfileData::optionalCollection),
                ProfileStatistics.CODEC.optionalFieldOf("statistics").forGetter(AlchemyProfileData::optionalStatistics)
        ).apply(codec, AlchemyProfileData::fromCodec));
    }
}
