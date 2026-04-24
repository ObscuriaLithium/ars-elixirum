package dev.obscuria.elixirum.common.world.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.registry.ElixirumParticles;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class SplashParticleOptions extends DustParticleOptionsBase {

    public static final Deserializer DESERIALIZER = new Deserializer();
    public static final Codec<SplashParticleOptions> CODEC;

    @Getter public final Vector3f color;
    @Getter public final float scale;

    public SplashParticleOptions(RGB color, float scale) {
        this(new Vector3f(color.red(), color.green(), color.blue()), scale);
    }

    public SplashParticleOptions(Vector3f color, float scale) {
        super(color, scale);
        this.color = color;
        this.scale = scale;
    }

    @Override
    public ParticleType<SplashParticleOptions> getType() {
        return ElixirumParticles.SPLASH.get();
    }

    public static class Deserializer implements ParticleOptions.Deserializer<SplashParticleOptions> {

        @Override
        public SplashParticleOptions fromCommand(
                ParticleType<SplashParticleOptions> type,
                StringReader reader) throws CommandSyntaxException {
            final var color = readVector3f(reader);
            reader.expect(' ');
            final var scale = reader.readFloat();
            return new SplashParticleOptions(color, scale);
        }

        @Override
        public SplashParticleOptions fromNetwork(
                ParticleType<SplashParticleOptions> type,
                FriendlyByteBuf byteBuf) {
            return new SplashParticleOptions(readVector3f(byteBuf), byteBuf.readFloat());
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ExtraCodecs.VECTOR3F.fieldOf("iconColor").forGetter(SplashParticleOptions::getColor),
                Codec.FLOAT.fieldOf("scale").forGetter(SplashParticleOptions::getScale)
        ).apply(codec, SplashParticleOptions::new));
    }
}
