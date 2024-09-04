package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EssenceStack(Holder<Essence> essenceHolder,
                           int weight) {
    public static final Codec<EssenceStack> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceStack> STREAM_CODEC;

    public Essence getEssence() {
        return essenceHolder.value();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Essence.CODEC.fieldOf("essence").forGetter(EssenceStack::essenceHolder),
                Codec.INT.fieldOf("weight").forGetter(EssenceStack::weight)
        ).apply(instance, EssenceStack::new));
        STREAM_CODEC = StreamCodec.composite(
                Essence.STREAM_CODEC, EssenceStack::essenceHolder,
                ByteBufCodecs.INT, EssenceStack::weight,
                EssenceStack::new);
    }
}
