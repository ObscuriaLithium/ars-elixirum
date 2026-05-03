package dev.obscuria.elixirum.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.obscuria.elixirum.common.world.particle.BubbleParticleOptions;
import dev.obscuria.fragmentum.util.easing.Easing;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class BubbleParticle extends Particle {

    private static final List<Vec3> CORNERS;

    private BubbleParticle(ClientLevel level,
                           BubbleParticleOptions options,
                           double x, double y, double z,
                           double xd, double yd, double zd) {
        super(level, x, y, z);

        setColor(options.color.x(), options.color.y(), options.color.z());
        setLifetime(80);

        this.gravity = -0.015f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.alpha = 0.5f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        final var timer = Mth.clamp((this.age + partialTicks) / this.lifetime, 0f, 1f);
        final var scale = 0.1f * Easing.EASE_OUT_CUBIC.reverse().compute(timer);
        this.alpha = Easing.EASE_IN_CUBIC.mergeOut(Easing.EASE_OUT_CUBIC, 0.1f).compute(timer);

        final var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        final var x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camera.getPosition().x());
        final var y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camera.getPosition().y());
        final var z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camera.getPosition().z());
        final var points = CORNERS.stream().map(it -> translate(it, x, y, z, scale)).toList();

        final var consumer = bufferSource.getBuffer(RenderType.gui());
        this.quad(consumer, points.get(3), points.get(2), points.get(1), points.get(0));
        this.quad(consumer, points.get(5), points.get(4), points.get(0), points.get(1));
        this.quad(consumer, points.get(4), points.get(5), points.get(6), points.get(7));
        this.quad(consumer, points.get(2), points.get(3), points.get(7), points.get(6));
        this.quad(consumer, points.get(1), points.get(2), points.get(6), points.get(5));
        this.quad(consumer, points.get(3), points.get(0), points.get(4), points.get(7));
        bufferSource.endBatch();
    }

    private Vec3 translate(Vec3 point, float x, float y, float z, float scale) {
        return new Vec3(point.x * scale + x, point.y * scale + y, point.z * scale + z);
    }

    private void quad(VertexConsumer buffer, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        buffer.vertex(v1.x(), v1.y(), v1.z()).color(this.rCol, this.gCol, this.bCol, this.alpha).endVertex();
        buffer.vertex(v2.x(), v2.y(), v2.z()).color(this.rCol, this.gCol, this.bCol, this.alpha).endVertex();
        buffer.vertex(v3.x(), v3.y(), v3.z()).color(this.rCol, this.gCol, this.bCol, this.alpha).endVertex();
        buffer.vertex(v4.x(), v4.y(), v4.z()).color(this.rCol, this.gCol, this.bCol, this.alpha).endVertex();
    }

    public static class Provider implements ParticleProvider<BubbleParticleOptions> {

        public Provider() {}

        @Override
        public Particle createParticle(
                BubbleParticleOptions options, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new BubbleParticle(level, options, x, y, z, xd, yd, zd);
        }
    }

    static {
        CORNERS = List.of(
                new Vec3(-1.0, -1.0, 1.0),
                new Vec3(-1.0, 1.0, 1.0),
                new Vec3(1.0, 1.0, 1.0),
                new Vec3(1.0, -1.0, 1.0),
                new Vec3(-1.0, -1.0, -1.0),
                new Vec3(-1.0, 1.0, -1.0),
                new Vec3(1.0, 1.0, -1.0),
                new Vec3(1.0, -1.0, -1.0));
    }
}
