package dev.obscuria.elixirum.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ElixirumLayers;
import dev.obscuria.elixirum.client.model.ModelGlassCauldron;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class GlassCauldronRenderer implements BlockEntityRenderer<GlassCauldronEntity> {
    private static final ResourceLocation CAULDRON_TEXTURE = Elixirum.key("textures/entity/cauldron.png");
    private static final ResourceLocation CAULDRON_GLASS_TEXTURE = Elixirum.key("textures/entity/cauldron_glass.png");
    private static final ResourceLocation CAULDRON_FLUID_TEXTURE = Elixirum.key("textures/entity/cauldron_fluid.png");
    private final ModelGlassCauldron modelCauldron;
    private final ModelPart modelFluid;

    public GlassCauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.modelCauldron = new ModelGlassCauldron(context.bakeLayer(ElixirumLayers.GLASS_CAULDRON));
        this.modelFluid = context.bakeLayer(ElixirumLayers.GLASS_CAULDRON_FLUID);
    }

    @Override
    public void render(GlassCauldronEntity entity, float partialTick, PoseStack pose, MultiBufferSource source, int light, int overlay) {
        pose.pushPose();
        this.setupPose(pose);
        this.animate(entity, partialTick);
        this.renderCauldron(pose, source, light, overlay);
        this.renderFluid(entity, pose, source, light);
        this.renderGlass(pose, source, light, overlay);
        pose.popPose();
    }

    private void setupPose(PoseStack pose) {
        pose.translate(0.5, 1.5, 0.5);
        pose.scale(-1, -1, 1);
    }

    private void animate(GlassCauldronEntity entity, float partialTick) {
        final var rotation = entity.getRotation(partialTick);
        //final var instability = entity.getInstability();
        this.modelCauldron.main.yRot = rotation;
        //this.modelCauldron.main.xRot = (float) (Math.cos(rotation * 5 * instability) * 0.05f * instability);
        //this.modelCauldron.main.zRot = (float) (Math.cos(rotation * 2 * instability) * 0.05f * instability);
    }

    private void renderCauldron(PoseStack pose, MultiBufferSource source, int light, int overlay) {
        this.modelCauldron.renderToBuffer(pose, this.getBuffer(source), light, overlay, 0xFFFFFFFF);
    }

    private void renderFluid(GlassCauldronEntity entity, PoseStack pose, MultiBufferSource source, int light) {
        final var contentType = entity.getContentType();
        if (contentType.isNone()) {
            this.modelFluid.visible = false;
        } else {
            pose.pushPose();
            this.modelFluid.visible = true;
            this.modelCauldron.translateFluid(pose);
            this.modelFluid.render(pose, this.getFluidBuffer(source),
                    contentType.isGlowing() ? LightTexture.FULL_BRIGHT : light,
                    OverlayTexture.NO_OVERLAY, contentType.getColor(entity));
            pose.popPose();
        }
    }

    private void renderGlass(PoseStack pose, MultiBufferSource source, int light, int overlay) {
        this.modelCauldron.renderToBuffer(pose, this.getGlassBuffer(source), light, overlay, 0xFFFFFFFF);
    }

    private VertexConsumer getBuffer(MultiBufferSource source) {
        return source.getBuffer(RenderType.entityCutoutNoCull(CAULDRON_TEXTURE));
    }

    private VertexConsumer getGlassBuffer(MultiBufferSource source) {
        return source.getBuffer(RenderType.entityTranslucent(CAULDRON_GLASS_TEXTURE));
    }

    private VertexConsumer getFluidBuffer(MultiBufferSource source) {
        return source.getBuffer(RenderType.entityTranslucent(CAULDRON_FLUID_TEXTURE));
    }
}
