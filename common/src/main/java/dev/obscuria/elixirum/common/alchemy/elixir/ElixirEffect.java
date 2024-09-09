package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ElixirEffect(Holder<Essence> essenceHolder,
                           double amplifierWeight,
                           double durationWeight,
                           int ingredients) {
    public static final Codec<ElixirEffect> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirEffect> STREAM_CODEC;

    public static ElixirEffect byWeight(Holder<Essence> essence, double amplifierWeight, double durationWeight) {
        return new ElixirEffect(essence,
                amplifierWeight,
                durationWeight,
                essence.value().requiredIngredients());
    }
    
    public static ElixirEffect byValue(Holder<Essence> essence, int amplifier, int duration) {
        return new ElixirEffect(essence,
                essence.value().amplifier().weightByValue(amplifier),
                essence.value().duration().weightByValue(duration),
                essence.value().requiredIngredients());
    }

    public Component getName() {
        return getEssence().getName();
    }

    public Essence getEssence() {
        return essenceHolder.value();
    }

    public int getQuality() {
        if (isInstantenous()) return (int) amplifierWeight;
        return (int) ((amplifierWeight + durationWeight) * 0.5);
    }

    public boolean isPale() {
        return ingredients < getEssence().requiredIngredients();
    }

    public boolean isWeak() {
        var essence = getEssence();
        return amplifierWeight <= essence.amplifier().minWeight()
                || durationWeight <= essence.duration().minWeight();
    }

    public boolean isInstantenous() {
        return getEssence().getEffect().isInstantenous();
    }

    public int getAmplifier() {
        return getEssence().amplifier().valueByWeight(amplifierWeight);
    }

    public int getDuration() {
        return getEssence().duration().valueByWeight(durationWeight);
    }

    public ElixirEffect scale(double scale) {
        final var instantenous = this.isInstantenous();
        final var amplifier = instantenous ? amplifierWeight() * scale : amplifierWeight();
        final var duration = instantenous ? durationWeight() : durationWeight() * scale;
        return new ElixirEffect(essenceHolder(), amplifier, duration, ingredients());
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Essence.CODEC.fieldOf("essence").forGetter(ElixirEffect::essenceHolder),
                Codec.DOUBLE.fieldOf("amplifierWeight").forGetter(ElixirEffect::amplifierWeight),
                Codec.DOUBLE.fieldOf("durationWeight").forGetter(ElixirEffect::durationWeight),
                Codec.INT.fieldOf("ingredients").forGetter(ElixirEffect::ingredients)
        ).apply(instance, ElixirEffect::new));
        STREAM_CODEC = StreamCodec.composite(
                Essence.STREAM_CODEC, ElixirEffect::essenceHolder,
                ByteBufCodecs.DOUBLE, ElixirEffect::amplifierWeight,
                ByteBufCodecs.DOUBLE, ElixirEffect::durationWeight,
                ByteBufCodecs.INT, ElixirEffect::ingredients,
                ElixirEffect::new);
    }
}
