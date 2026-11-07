package dev.obscuria.elixirum.client.particles;

import dev.obscuria.elixirum.common.world.particle.SplashParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;

public final class SplashParticle extends WaterDropParticle {

    private SplashParticle(ClientLevel level,
                             SplashParticleOptions options,
                             double x, double y, double z,
                             double xd, double yd, double zd,
                             SpriteSet sprites) {
        super(level, x, y, z);

        pickSprite(sprites);
        setColor(options.color.x(), options.color.y(), options.color.z());
        scale(options.scale);

        this.gravity = 0.04f;
        if (yd == 0.0 && (xd != 0.0 || zd != 0.0)) {
            this.xd = xd;
            this.yd = 0.1;
            this.zd = zd;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SplashParticleOptions> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SplashParticleOptions options, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new SplashParticle(level, options, x, y, z, xd, yd, zd, sprites);
        }
    }
}
