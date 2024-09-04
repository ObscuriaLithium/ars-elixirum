package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ElixirStyle(ElixirStyles.Shape shape,
                          ElixirStyles.Cap cap) {
    public static final Codec<ElixirStyle> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirStyle> STREAM_CODEC;

    public static ElixirStyle create(int shapeId, int capId) {
        return new ElixirStyle(ElixirStyles.Shape.BOTTLE_1, ElixirStyles.Cap.WOOD);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("shape").forGetter(style -> style.shape().getId()),
                Codec.INT.fieldOf("cap").forGetter(style -> style.cap().getId())
        ).apply(instance, ElixirStyle::create));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, style -> style.shape().getId(),
                ByteBufCodecs.INT, style -> style.cap().getId(),
                ElixirStyle::create);
    }
}
