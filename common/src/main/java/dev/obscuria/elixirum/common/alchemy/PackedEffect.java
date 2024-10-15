package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;

public record PackedEffect(Holder<Essence> essenceHolder,
                           double amplifierWeight,
                           double durationWeight,
                           int ingredients)
{

    public static final Codec<PackedEffect> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, PackedEffect> STREAM_CODEC;

    public static PackedEffect byWeight(Holder<Essence> essence, double amplifierWeight, double durationWeight)
    {
        return new PackedEffect(essence,
                amplifierWeight,
                durationWeight,
                essence.value().requiredIngredients());
    }

    public static PackedEffect byValue(Holder<Essence> essence, int amplifier, int duration)
    {
        return new PackedEffect(essence,
                essence.value().weightForAmplifier(amplifier),
                essence.value().weightForDuration(duration),
                essence.value().requiredIngredients());
    }

    public PackedEffect(Holder<Essence> essenceHolder,
                        double amplifierWeight,
                        double durationWeight,
                        int ingredients)
    {
        this.essenceHolder = essenceHolder;
        this.amplifierWeight = Math.clamp(amplifierWeight, 0.0, 100.0);
        this.durationWeight = Math.clamp(durationWeight, 0.0, 100.0);
        this.ingredients = Math.max(ingredients, 0);
    }

    public MobEffectInstance instantiate()
    {
        return instantiate(0.0, 0.0);
    }

    public MobEffectInstance instantiate(double mastery, double immunity)
    {
        final var rawDuration = 20 * getDuration();
        final var duration = getEssence().getEffect().isBeneficial()
                ? Math.clamp(rawDuration + 20 * mastery, rawDuration * 0.5, rawDuration * 1.5)
                : Math.clamp(rawDuration - 20 * immunity, rawDuration * 0.5, rawDuration * 1.5);
        return new MobEffectInstance(getEssence().effectHolder(), (int) duration, getAmplifier());
    }

    public Essence getEssence()
    {
        return essenceHolder.value();
    }

    public int getQuality()
    {
        if (isInstantenous()) return (int) amplifierWeight;
        return (int) ((amplifierWeight + durationWeight) * 0.5);
    }

    public boolean isWeak()
    {
        return getQuality() < getEssence().requiredQuality();
    }

    public boolean isPale()
    {
        return ingredients < getEssence().requiredIngredients();
    }

    public boolean isInstantenous()
    {
        return getEssence().getEffect().isInstantenous();
    }

    public int getAmplifier()
    {
        return getEssence().amplifierByWeight(amplifierWeight);
    }

    public int getDuration()
    {
        return getEssence().durationByWeight(durationWeight);
    }

    public PackedEffect scale(double scale)
    {
        final var instantenous = this.isInstantenous();
        final var amplifier = instantenous ? amplifierWeight() * scale : amplifierWeight();
        final var duration = instantenous ? durationWeight() : durationWeight() * scale;
        return new PackedEffect(essenceHolder(), amplifier, duration, ingredients());
    }

    public Component getName()
    {
        return getEssence().getDisplayName();
    }

    public Component getDisplayName()
    {
        final var amplifier = getAmplifier();
        if (amplifier > 0)
        {
            final var potency = Component.translatable("potion.potency." + amplifier);
            return Component.translatable("potion.withAmplifier", getName(), potency);
        }
        return getName();
    }

    public Component getStatusOrDuration(float tickRate)
    {
        return this.isPale()
                ? Component.translatable("elixir.status.pale")
                : this.isWeak()
                ? Component.translatable("elixir.status.weak")
                : this.isInstantenous()
                ? Component.translatable("elixir.status.instantenous")
                : this.getFormattedDuration(tickRate);
    }

    public Component getFormattedDuration(float tickRate)
    {
        final var ticks = Mth.floor(20 * getDuration());
        return Component.literal(StringUtil.formatTickDuration(ticks, tickRate));
    }

    public ChatFormatting getColor()
    {
        return !isPale() && !isWeak()
                ? getEssence().getEffect().getCategory().getTooltipFormatting()
                : ChatFormatting.GRAY;
    }

    static
    {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Essence.CODEC.fieldOf("essence").forGetter(PackedEffect::essenceHolder),
                Codec.DOUBLE.fieldOf("amplifierWeight").forGetter(PackedEffect::amplifierWeight),
                Codec.DOUBLE.fieldOf("durationWeight").forGetter(PackedEffect::durationWeight),
                Codec.INT.fieldOf("ingredients").forGetter(PackedEffect::ingredients)
        ).apply(instance, PackedEffect::new));
        STREAM_CODEC = StreamCodec.composite(
                Essence.STREAM_CODEC, PackedEffect::essenceHolder,
                ByteBufCodecs.DOUBLE, PackedEffect::amplifierWeight,
                ByteBufCodecs.DOUBLE, PackedEffect::durationWeight,
                ByteBufCodecs.INT, PackedEffect::ingredients,
                PackedEffect::new);
    }
}
