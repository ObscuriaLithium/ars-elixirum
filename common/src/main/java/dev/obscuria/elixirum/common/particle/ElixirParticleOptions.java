package dev.obscuria.elixirum.common.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public abstract class ElixirParticleOptions implements ParticleOptions
{
    private final Vector3f color;
    private final float scale;

    protected ElixirParticleOptions(Vector3f color, float scale)
    {
        this.color = color;
        this.scale = Mth.clamp(scale, 0.01F, 4.0F);
    }

    public Vector3f getColor()
    {
        return this.color;
    }

    public float getScale()
    {
        return this.scale;
    }
}
