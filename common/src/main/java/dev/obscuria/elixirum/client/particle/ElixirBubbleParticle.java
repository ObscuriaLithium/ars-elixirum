package dev.obscuria.elixirum.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.obscuria.core.api.v1.common.easing.Easing;
import dev.obscuria.elixirum.common.particle.ElixirBubbleParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;

public final class ElixirBubbleParticle extends Particle
{
    private static final List<Vector3f> RAW_POINTS;

    private ElixirBubbleParticle(ClientLevel level, ElixirBubbleParticleOptions options,
                                 double x, double y, double z,
                                 double xd, double yd, double zd)
    {
        super(level, x, y, z);
        this.setColor(options.getColor().x, options.getColor().y, options.getColor().z);
        this.setLifetime(80);
        this.gravity = -0.015f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.alpha = 0.5f;
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float delta)
    {
        final var lifeFactor = Mth.clamp((this.age + delta) / this.lifetime, 0, 1);
        final var scale = 0.1f * Easing.EASE_OUT_CUBIC.reversed().compute(lifeFactor);
        this.alpha = Easing.EASE_IN_CUBIC.mergeOut(Easing.EASE_OUT_CUBIC, 0.1f).compute(lifeFactor);

        final var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        final var cameraPos = camera.getPosition();
        final var x = (float) (Mth.lerp(delta, this.xo, this.x) - cameraPos.x());
        final var y = (float) (Mth.lerp(delta, this.yo, this.y) - cameraPos.y());
        final var z = (float) (Mth.lerp(delta, this.zo, this.z) - cameraPos.z());
        final var points = RAW_POINTS.stream()
                .map(point -> new Vector3f(
                        point.x * scale + x,
                        point.y * scale + y,
                        point.z * scale + z))
                .toArray(Vector3f[]::new);

        final var consumer = bufferSource.getBuffer(RenderType.gui());
        this.quad(consumer, points[3], points[2], points[1], points[0]);
        this.quad(consumer, points[5], points[4], points[0], points[1]);
        this.quad(consumer, points[4], points[5], points[6], points[7]);
        this.quad(consumer, points[2], points[3], points[7], points[6]);
        this.quad(consumer, points[1], points[2], points[6], points[5]);
        this.quad(consumer, points[3], points[0], points[4], points[7]);
        bufferSource.endBatch();
    }

    private void quad(VertexConsumer consumer, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4)
    {
        consumer.addVertex(v1.x(), v1.y(), v1.z()).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
        consumer.addVertex(v2.x(), v2.y(), v2.z()).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
        consumer.addVertex(v3.x(), v3.y(), v3.z()).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
        consumer.addVertex(v4.x(), v4.y(), v4.z()).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.CUSTOM;
    }


    public static class Provider implements ParticleProvider<ElixirBubbleParticleOptions>
    {

        public Provider() {}

        public Particle createParticle(ElixirBubbleParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd)
        {
            return new ElixirBubbleParticle(level, options, x, y, z, xd, yd, zd);
        }
    }

    static
    {
        RAW_POINTS = List.of(
                new Vector3f(-1.0F, -1.0F, 1.0F),
                new Vector3f(-1.0F, 1.0F, 1.0F),
                new Vector3f(1.0F, 1.0F, 1.0F),
                new Vector3f(1.0F, -1.0F, 1.0F),
                new Vector3f(-1.0F, -1.0F, -1.0F),
                new Vector3f(-1.0F, 1.0F, -1.0F),
                new Vector3f(1.0F, 1.0F, -1.0F),
                new Vector3f(1.0F, -1.0F, -1.0F));
    }
}
