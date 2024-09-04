package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.effect.MobEffect;

public record Essence(Holder<MobEffect> effectHolder,
                      int requiredIngredients) {
    public static final Codec<Essence> DIRECT_CODEC;
    public static final Codec<Holder<Essence>> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Essence>> STREAM_CODEC;

    public MobEffect getEffect() {
        return this.effectHolder.value();
    }

    public Component getName() {
        return getEffect().getDisplayName();
    }

    static {
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEffect.CODEC.fieldOf("mob_effect").forGetter(Essence::effectHolder),
                Codec.INT.fieldOf("required_ingredients").forGetter(Essence::requiredIngredients)
        ).apply(instance, Essence::new));
        CODEC = RegistryFixedCodec.create(ElixirumRegistries.ESSENCE);
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ElixirumRegistries.ESSENCE);
    }
}
