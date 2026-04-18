package dev.obscuria.elixirum.server.alchemy.resources.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.ingredient.properties.StabilizerProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.List;

public record FixedStabilizerPropertyProvider(
        List<Aspect> purgeTargets
) implements IStabilizerPropertyProvider {

    public static final Codec<FixedStabilizerPropertyProvider> CODEC;

    @Override
    public Codec<FixedStabilizerPropertyProvider> codec() {
        return CODEC;
    }

    @Override
    public StabilizerProperties resolve(Item item, RandomSource random) {
        return new StabilizerProperties(purgeTargets);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Aspect.CODEC.listOf().fieldOf("purge_targets").forGetter(FixedStabilizerPropertyProvider::purgeTargets)
        ).apply(codec, FixedStabilizerPropertyProvider::new));
    }
}
