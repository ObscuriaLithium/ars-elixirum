package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ElixirEffect(Holder<Essence> essenceHolder,
                           int ingredients,
                           double amplifier,
                           double duration) {
    public static final Codec<ElixirEffect> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirEffect> STREAM_CODEC;

    public Essence getEssence() {
        return essenceHolder().value();
    }

    public int getQuality() {
        if (isInstantenous()) return (int) Math.round(amplifier);
        return (int) Math.round((amplifier + duration) * 0.5);
    }

    public boolean isPale() {
        return this.ingredients < getEssence().requiredIngredients();
    }

    public boolean isInstantenous() {
        return getEssence().getEffect().isInstantenous();
    }

    public ElixirEffect scale(double scale) {
        final var instantenous = this.isInstantenous();
        final var amplifier = instantenous ? amplifier() * scale : amplifier();
        final var duration = instantenous ? duration() : duration() * scale;
        return new ElixirEffect(essenceHolder(), ingredients(), amplifier, duration);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Essence.CODEC.fieldOf("essence").forGetter(ElixirEffect::essenceHolder),
                Codec.INT.fieldOf("ingredients").forGetter(ElixirEffect::ingredients),
                Codec.DOUBLE.fieldOf("amplifier").forGetter(ElixirEffect::amplifier),
                Codec.DOUBLE.fieldOf("duration").forGetter(ElixirEffect::duration)
        ).apply(instance, ElixirEffect::new));
        STREAM_CODEC = StreamCodec.composite(
                Essence.STREAM_CODEC, ElixirEffect::essenceHolder,
                ByteBufCodecs.INT, ElixirEffect::ingredients,
                ByteBufCodecs.DOUBLE, ElixirEffect::amplifier,
                ByteBufCodecs.DOUBLE, ElixirEffect::duration,
                ElixirEffect::new);
    }
}
