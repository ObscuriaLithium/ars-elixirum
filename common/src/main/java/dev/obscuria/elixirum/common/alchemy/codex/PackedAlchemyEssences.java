package dev.obscuria.elixirum.common.alchemy.codex;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record PackedAlchemyEssences(
        Map<ResourceLocation, Essence> essences
) {

    public static final Codec<PackedAlchemyEssences> CODEC;
    public static final PackedAlchemyEssences EMPTY;

    static {
        CODEC = Codec
                .unboundedMap(ResourceLocation.CODEC, Essence.CODEC)
                .xmap(PackedAlchemyEssences::new, PackedAlchemyEssences::essences);
        EMPTY = new PackedAlchemyEssences(Map.of());
    }
}
