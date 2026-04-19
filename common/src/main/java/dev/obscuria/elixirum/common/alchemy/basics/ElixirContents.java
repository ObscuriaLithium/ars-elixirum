package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ElixirContents(
        List<EffectProvider> providers,
        RGB color
) {

    public static final ElixirContents EMPTY;
    public static final ElixirContents WATER;
    public static final Codec<ElixirContents> CODEC;

    public Component displayName() {
        return providers.isEmpty()
                ? Component.literal("Water")
                : providers.get(0).displayName();
    }

    public double temper() {
        if (providers.isEmpty()) return Temper.BALANCED.value;
        if (providers.get(0) instanceof EffectProvider.Packed packed) return packed.temper();
        return Temper.BALANCED.value;
    }

    public Aspect aspect() {
        return providers.isEmpty() ? Aspect.NONE : providers.get(0).holder().map(Essence::aspect).orElse(Aspect.NONE);
    }

    public double quality() {
        return providers.isEmpty() ? 0.0 : providers.get(0).quality();
    }

    public boolean isVoided() {
        return providers.isEmpty() || providers.stream().allMatch(EffectProvider::isVoided);
    }

    public void apply(LivingEntity target, @Nullable Entity direct, @Nullable Entity source) {
        for (var provider : providers) {
            if (provider.isVoided()) continue;
            var instance = provider.instantiate(0, 0);
            if (instance.getEffect().isInstantenous()) {
                instance.getEffect().applyInstantenousEffect(direct, source, target, instance.getAmplifier(), 1);
            } else {
                target.addEffect(instance);
            }
        }
    }

    public static ElixirContents sorted(List<EffectProvider> providers) {
        var sortedProviders = sort(providers);
        return new ElixirContents(sortedProviders,
                sortedProviders.isEmpty()
                        ? ElixirContents.WATER.color()
                        : sortedProviders.get(0).color());
    }

    public static ElixirContents sorted(List<EffectProvider> providers, RGB color) {
        return new ElixirContents(sort(providers), color);
    }

    private static List<EffectProvider> sort(List<EffectProvider> providers) {
        return providers.stream().sorted().toList();
    }

    static {
        EMPTY = new ElixirContents(List.of(), Colors.rgbOf(0xffffff));
        WATER = new ElixirContents(List.of(), GlassCauldronEntity.ContentType.COLOR_WATER);
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                EffectProvider.CODEC.listOf().fieldOf("providers").forGetter(ElixirContents::providers),
                RGB.CODEC.fieldOf("color").forGetter(ElixirContents::color)
        ).apply(codec, ElixirContents::sorted));
    }
}
