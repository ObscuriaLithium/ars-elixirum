package dev.obscuria.elixirum.common.alchemy.codex;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.archivist.api.v1.components.ComponentKey;

import java.util.Map;

public record PackedAlchemyProfile(
        Map<ComponentKey<?>, Object> components
) {

    public static final Codec<PackedAlchemyProfile> CODEC;
    public static final PackedAlchemyProfile EMPTY;

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ComponentKey.createValueMapCodec(ProfileComponents.CODEC).fieldOf("components").forGetter(PackedAlchemyProfile::components)
        ).apply(codec, PackedAlchemyProfile::new));
        EMPTY = new PackedAlchemyProfile(Map.of());
    }
}
