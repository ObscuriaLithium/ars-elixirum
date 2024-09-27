package dev.obscuria.elixirum.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.obscuria.elixirum.common.block.PotionShelfBlock;
import dev.obscuria.elixirum.common.block.entity.PotionShelfEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class PotionShelfRenderer implements BlockEntityRenderer<PotionShelfEntity> {
    private final ItemRenderer itemRenderer;

    public PotionShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PotionShelfEntity entity, float f, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {
        final var firstStack = entity.getFirstStack();
        final var secondStack = entity.getSecondStack();
        final var thirdStack = entity.getThirdStack();
        if (firstStack.isEmpty() && secondStack.isEmpty() && thirdStack.isEmpty()) return;

        pose.pushPose();
        this.setupPose(entity, pose);
        this.renderStack(0, firstStack, light, overlay, pose, bufferSource);
        this.renderStack(-0.53, secondStack, light, overlay, pose, bufferSource);
        this.renderStack(-1.06, thirdStack, light, overlay, pose, bufferSource);
        pose.popPose();
    }

    private void setupPose(PotionShelfEntity entity, PoseStack pose) {
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(PotionShelfBlock.FACING).toYRot() + 180));
        pose.scale(0.6f, 0.6f, 0.6f);
        pose.translate(0.55, 0.65, 0.475);
    }

    private void renderStack(double offset, ItemStack stack, int light, int overlay, PoseStack pose, MultiBufferSource bufferSource) {
        if (stack.isEmpty()) return;
        pose.pushPose();
        pose.translate(offset, 0, 0);
        pose.mulPose(Axis.YP.rotationDegrees(25));
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, bufferSource, null, 0);
        pose.popPose();
    }
}
