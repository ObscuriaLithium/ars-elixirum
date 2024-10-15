package dev.obscuria.elixirum.client.particle;

import dev.obscuria.elixirum.common.particle.ElixirSplashParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;

public final class ElixirSplashParticle extends WaterDropParticle
{

    private ElixirSplashParticle(ClientLevel level,
                                 ElixirSplashParticleOptions options,
                                 double x, double y, double z,
                                 double xd, double yd, double zd,
                                 SpriteSet sprites)
    {
        super(level, x, y, z);
        this.pickSprite(sprites);
        this.setColor(options.getColor().x(), options.getColor().y(), options.getColor().z());
        this.scale(options.getScale());
        this.gravity = 0.04F;
        if (yd == 0.0D && (xd != 0.0D || zd != 0.0D))
        {
            this.xd = xd;
            this.yd = 0.1D;
            this.zd = zd;
        }
    }

    @Override
    protected int getLightColor(float value)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider implements ParticleProvider<ElixirSplashParticleOptions>
    {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        public Particle createParticle(ElixirSplashParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd)
        {
            return new ElixirSplashParticle(level, options, x, y, z, xd, yd, zd, sprites);
        }
    }
}