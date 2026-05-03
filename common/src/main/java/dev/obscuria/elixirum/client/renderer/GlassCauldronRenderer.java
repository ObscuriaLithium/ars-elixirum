package dev.obscuria.elixirum.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.models.ElixirumModelLayers;
import dev.obscuria.elixirum.client.models.ModelGlassCauldron;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class GlassCauldronRenderer implements BlockEntityRenderer<GlassCauldronEntity> {

    private static final ResourceLocation CAULDRON_TEXTURE = ArsElixirum.identifier("textures/entity/cauldron.png");
    private static final ResourceLocation CAULDRON_GLASS_TEXTURE = ArsElixirum.identifier("textures/entity/cauldron_glass.png");
    private static final ResourceLocation CAULDRON_FLUID_TEXTURE = ArsElixirum.identifier("textures/entity/cauldron_fluid.png");

    private final ModelGlassCauldron modelCauldron;
    private final ModelGlassCauldron modelFluid;

    public GlassCauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.modelCauldron = new ModelGlassCauldron(context.bakeLayer(ElixirumModelLayers.GLASS_CAULDRON));
        this.modelFluid = new ModelGlassCauldron(context.bakeLayer(ElixirumModelLayers.GLASS_CAULDRON_FLUID));
    }

    @Override
    public void render(GlassCauldronEntity entity, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.scale(-1f, -1f, 1f);
        this.animate(entity, partialTick);
        this.renderCauldronFluid(entity, pose, bufferSource, light);
        this.renderCauldron(pose, bufferSource, light, overlay);
        this.renderCauldronGlass(pose, bufferSource, light, overlay);
        pose.popPose();
    }

    private void animate(GlassCauldronEntity entity, float partialTick) {
        modelCauldron.main.yRot = entity.computeYRot(partialTick);
    }

    private void renderCauldronFluid(GlassCauldronEntity entity, PoseStack pose, MultiBufferSource bufferSource, int light) {
        final var contentType = entity.contentType();
        if (contentType.isNone()) return;
        pose.pushPose();
        modelCauldron.translateFluid(pose);
        final var color = contentType.pickColor(entity);
        light = contentType.isGlowing() ? LightTexture.FULL_BRIGHT : light;
        modelFluid.renderToBuffer(pose, cauldronFluidBuffer(bufferSource), light, OverlayTexture.NO_OVERLAY, color.red(), color.green(), color.blue(), 1f);
        pose.popPose();
    }

    private void renderCauldron(PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {
        modelCauldron.renderToBuffer(pose, cauldronBuffer(bufferSource), light, overlay, 1f, 1f, 1f, 1f);
    }

    private void renderCauldronGlass(PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {
        modelCauldron.renderToBuffer(pose, cauldronGlassBuffer(bufferSource), light, overlay, 1f, 1f, 1f, 1f);
    }

    private VertexConsumer cauldronBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.entityCutoutNoCull(CAULDRON_TEXTURE));
    }

    private VertexConsumer cauldronGlassBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.entityTranslucent(CAULDRON_GLASS_TEXTURE));
    }

    private VertexConsumer cauldronFluidBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.entityTranslucent(CAULDRON_FLUID_TEXTURE));
    }

    public static void translateOutline(PoseStack pose, @Nullable ClientLevel level, Camera camera, BlockPos pos, Runnable render) {
        if (level != null && level.getBlockEntity(pos) instanceof GlassCauldronEntity entity) {
            final var rotation = entity.computeYRot(Minecraft.getInstance().getFrameTime());
            final var offset = pos.getCenter().subtract(camera.getPosition());
            pose.pushPose();
            pose.translate(offset.x, offset.y, offset.z);
            pose.mulPose(Axis.YN.rotation(rotation));
            pose.translate(-offset.x, -offset.y, -offset.z);
            render.run();
            pose.popPose();
        } else {
            render.run();
        }
    }
}
