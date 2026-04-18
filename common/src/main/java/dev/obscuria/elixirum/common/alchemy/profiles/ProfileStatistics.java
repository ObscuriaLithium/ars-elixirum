package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import net.minecraft.core.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ProfileStatistics(
        Map<UUID, Integer> totalBrewed
) {

    public static final Codec<ProfileStatistics> CODEC;

    public void brewed(AlchemyRecipe recipe) {
        if (totalBrewed.containsKey(recipe.uuid())) {
            totalBrewed.put(recipe.uuid(), totalBrewed.get(recipe.uuid()) + 1);
        } else {
            totalBrewed.put(recipe.uuid(), 1);
        }
    }

    public int getTimesBrewed(AlchemyRecipe recipe) {
        return totalBrewed.getOrDefault(recipe.uuid(), 0);
    }

    public static ProfileStatistics empty() {
        return new ProfileStatistics(new HashMap<>());
    }

    private static ProfileStatistics fromCodec(Map<UUID, Integer> totalBrewed) {
        return new ProfileStatistics(new HashMap<>(totalBrewed));
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("total_brewed").forGetter(ProfileStatistics::totalBrewed)
        ).apply(codec, ProfileStatistics::fromCodec));
    }
}
