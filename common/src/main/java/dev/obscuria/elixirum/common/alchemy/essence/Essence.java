package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.ElixirumTags;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

public record Essence(Holder<MobEffect> effectHolder,
                      EssenceCategory category,
                      int maxAmplifier,
                      int maxDuration,
                      int requiredQuality,
                      int requiredIngredients)
{
    public static final Codec<Essence> DIRECT_CODEC;
    public static final Codec<Holder<Essence>> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Essence>> STREAM_CODEC;
    private static final double MAX_WEIGHT = 100.0;

    public static ResourceKey<Essence> key(ResourceLocation id)
    {
        return ResourceKey.create(ElixirumRegistries.ESSENCE, id);
    }

    public MobEffect getEffect()
    {
        return this.effectHolder.value();
    }

    public Component getDisplayName()
    {
        return getEffect().getDisplayName();
    }

    public int amplifierByWeight(double weight)
    {
        return valueByWeight(weight, maxAmplifier);
    }

    public int durationByWeight(double weight)
    {
        return valueByWeight(weight, maxDuration);
    }

    public double weightForAmplifier(int amplifier)
    {
        return weightByValue(amplifier, maxAmplifier);
    }

    public double weightForDuration(int duration)
    {
        return weightByValue(duration, maxDuration);
    }

    @SuppressWarnings("deprecation")
    public static boolean isBlacklisted(Item item)
    {
        return item.getClass().isAnnotationPresent(EssenceBlacklist.class)
                || item.builtInRegistryHolder().is(ElixirumTags.Items.ESSENCE_BLACKLIST);
    }

    @SuppressWarnings("deprecation")
    public static boolean isWhitelisted(Item item)
    {
        return item.getClass().isAnnotationPresent(EssenceWhitelist.class)
                || item.builtInRegistryHolder().is(ElixirumTags.Items.ESSENCE_WHITELIST);
    }

    private static int valueByWeight(double weight, int maxValue)
    {
        if (weight >= MAX_WEIGHT) return maxValue;
        if (weight <= 0) return 0;
        final var ratio = weight / MAX_WEIGHT;
        return (int) (maxValue * (ratio * ratio));
    }

    private static double weightByValue(int value, int maxValue)
    {
        if (value <= 0) return 0;
        if (value >= maxValue) return MAX_WEIGHT;
        double ratio = Math.sqrt((double) value / maxValue);
        return MAX_WEIGHT * ratio + 0.001;
    }

    static
    {
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEffect.CODEC.fieldOf("mob_effect").forGetter(Essence::effectHolder),
                EssenceCategory.CODEC.optionalFieldOf("category", EssenceCategory.NONE).forGetter(Essence::category),
                Codec.INT.fieldOf("max_amplifier").forGetter(Essence::maxAmplifier),
                Codec.INT.fieldOf("max_duration").forGetter(Essence::maxDuration),
                Codec.INT.fieldOf("required_quality").forGetter(Essence::requiredQuality),
                Codec.INT.fieldOf("required_ingredients").forGetter(Essence::requiredIngredients)
        ).apply(instance, Essence::new));
        CODEC = RegistryFixedCodec.create(ElixirumRegistries.ESSENCE);
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ElixirumRegistries.ESSENCE);
    }
}
