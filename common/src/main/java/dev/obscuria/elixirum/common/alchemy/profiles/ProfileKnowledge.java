package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;

public record ProfileKnowledge() {

    public static final Codec<ProfileKnowledge> CODEC;

    public static ProfileKnowledge empty() {
        return new ProfileKnowledge();
    }

    public void updateFrom(ProfileKnowledge other) {}

    static {
        CODEC = Codec.unit(ProfileKnowledge::empty);
    }
}
