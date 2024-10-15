package dev.obscuria.elixirum.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.registry.ElixirumParticleTypes;
import dev.obscuria.core.api.v1.common.ObscureFactory;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import org.joml.Vector3f;

public class ElixirBubbleParticleOptions extends ElixirParticleOptions
{
    private static final MapCodec<ElixirBubbleParticleOptions> CODEC;
    private static final StreamCodec<RegistryFriendlyByteBuf, ElixirBubbleParticleOptions> STREAM_CODEC;
    public static final ParticleType<ElixirBubbleParticleOptions> TYPE;

    public ElixirBubbleParticleOptions(Vector3f color)
    {
        this(color, 1f);
    }

    public ElixirBubbleParticleOptions(Vector3f color, float scale)
    {
        super(color, scale);
    }

    public static ElixirBubbleParticleOptions parse(int color)
    {
        return new ElixirBubbleParticleOptions(new Vector3f(
                FastColor.ARGB32.red(color) / 255f,
                FastColor.ARGB32.green(color) / 255f,
                FastColor.ARGB32.blue(color) / 255f));
    }

    public ParticleType<ElixirBubbleParticleOptions> getType()
    {
        return ElixirumParticleTypes.ELIXIR_BUBBLE.value();
    }

    static
    {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(ElixirParticleOptions::getColor),
                Codec.FLOAT.fieldOf("scale").forGetter(ElixirParticleOptions::getScale)
        ).apply(instance, ElixirBubbleParticleOptions::new));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, ElixirParticleOptions::getColor,
                ByteBufCodecs.FLOAT, ElixirParticleOptions::getScale,
                ElixirBubbleParticleOptions::new);
        TYPE = ObscureFactory.createParticleType(false, CODEC, STREAM_CODEC);
    }
}
