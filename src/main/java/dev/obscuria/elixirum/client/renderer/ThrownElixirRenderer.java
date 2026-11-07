package dev.obscuria.elixirum.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.obscuria.elixirum.common.world.entity.ThrownElixirProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;

public final class ThrownElixirRenderer extends EntityRenderer<ThrownElixirProjectile> {

    private final ItemRenderer itemRenderer;

    public ThrownElixirRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(ThrownElixirProjectile entity, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource bufferSource, int light) {

        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            pose.pushPose();
            pose.translate(0, 0.12f, 0);
            pose.mulPose(Axis.YP.rotationDegrees(yaw));
            pose.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTick) * 14f));
            pose.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTick) * 4f));
            pose.pushPose();
            pose.translate(0, -0.12f, 0);
            this.itemRenderer.renderStatic(entity.getItem(),
                    ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY,
                    pose, bufferSource, entity.level(), entity.getId());
            pose.popPose();
            pose.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownElixirProjectile entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}