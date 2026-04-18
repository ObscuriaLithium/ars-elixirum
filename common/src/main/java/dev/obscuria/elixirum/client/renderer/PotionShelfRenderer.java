package dev.obscuria.elixirum.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.obscuria.elixirum.common.world.block.PotionShelfBlock;
import dev.obscuria.elixirum.common.world.block.entity.PotionShelfEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PotionShelfRenderer implements BlockEntityRenderer<PotionShelfEntity> {

    private final ItemRenderer itemRenderer;

    public PotionShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            PotionShelfEntity entity, float delta, PoseStack pose,
            MultiBufferSource bufferSource, int light, int overlay
    ) {
        var firstStack = entity.getItem(0);
        var secondStack = entity.getItem(1);
        var thirdStack = entity.getItem(2);

        if (firstStack.isEmpty() && secondStack.isEmpty() && thirdStack.isEmpty()) return;

        pose.pushPose();
        setupPose(entity, pose);
        renderStack(0.0, firstStack, light, overlay, pose, bufferSource);
        renderStack(-0.53, secondStack, light, overlay, pose, bufferSource);
        renderStack(-1.06, thirdStack, light, overlay, pose, bufferSource);
        pose.popPose();
    }

    private void setupPose(PotionShelfEntity entity, PoseStack pose) {
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(PotionShelfBlock.FACING).toYRot() + 180));
        pose.scale(0.6f, 0.6f, 0.6f);
        pose.translate(0.55, 0.65, 0.475);
    }

    private void renderStack(
            double offset,
            ItemStack stack,
            int light,
            int overlay,
            PoseStack pose,
            MultiBufferSource bufferSource
    ) {
        if (stack.isEmpty()) return;
        pose.pushPose();
        pose.translate(offset, 0.0, 0.0);
        pose.mulPose(Axis.YP.rotationDegrees(25f));
        itemRenderer.renderStatic(
                stack, ItemDisplayContext.FIXED, light, overlay,
                pose, bufferSource, null, 0);
        pose.popPose();
    }
}
