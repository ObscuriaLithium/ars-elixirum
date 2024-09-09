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
                      Property amplifier,
                      Property duration,
                      int requiredIngredients) {
    public static final Codec<Essence> DIRECT_CODEC;
    public static final Codec<Holder<Essence>> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Essence>> STREAM_CODEC;

    public static ResourceKey<Essence> key(ResourceLocation id) {
        return ResourceKey.create(ElixirumRegistries.ESSENCE, id);
    }

    public MobEffect getEffect() {
        return this.effectHolder.value();
    }

    public Component getName() {
        return getEffect().getDisplayName();
    }

    @SuppressWarnings("deprecation")
    public static boolean isBlacklisted(Item item) {
        return item.getClass().isAnnotationPresent(EssenceBlacklist.class)
                || item.builtInRegistryHolder().is(ElixirumTags.Items.ESSENCE_BLACKLIST);
    }

    @SuppressWarnings("deprecation")
    public static boolean isWhitelisted(Item item) {
        return item.getClass().isAnnotationPresent(EssenceWhitelist.class)
                || item.builtInRegistryHolder().is(ElixirumTags.Items.ESSENCE_WHITELIST);
    }

    public record Property(int maxValue, double minWeight, double maxWeight) {

        public int valueByWeight(double weight) {
            if (weight >= maxWeight) return maxValue;
            if (weight <= 0) return 0;
            final var ratio = weight / maxWeight;
            return (int) (maxValue * (ratio * ratio));
        }

        public double weightByValue(int value) {
            if (value <= 0) return 0;
            if (value >= maxValue) return maxWeight;
            double ratio = Math.sqrt((double) value / maxValue);
            return maxWeight * ratio + 0.01;
        }
    }

    static {
        final var propertyCodec = RecordCodecBuilder.<Property>create(instance -> instance.group(
                Codec.INT.fieldOf("max_value").forGetter(Property::maxValue),
                Codec.DOUBLE.fieldOf("min_weight").forGetter(Property::minWeight),
                Codec.DOUBLE.fieldOf("max_weight").forGetter(Property::maxWeight)
        ).apply(instance, Property::new));
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEffect.CODEC.fieldOf("mob_effect").forGetter(Essence::effectHolder),
                propertyCodec.fieldOf("amplifier").forGetter(Essence::amplifier),
                propertyCodec.fieldOf("duration").forGetter(Essence::duration),
                Codec.INT.fieldOf("required_ingredients").forGetter(Essence::requiredIngredients)
        ).apply(instance, Essence::new));
        CODEC = RegistryFixedCodec.create(ElixirumRegistries.ESSENCE);
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ElixirumRegistries.ESSENCE);
    }
}
