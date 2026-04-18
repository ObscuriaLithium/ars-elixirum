package dev.obscuria.elixirum.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.obscuria.elixirum.client.renderer.GlassCauldronRenderer;
import dev.obscuria.elixirum.common.registry.ElixirumBlocks;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Shadow private ClientLevel level;
    @Unique @Nullable private Camera elixirum$cachedCamera;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void renderLevel(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline,
                             Camera camera, GameRenderer gameRenderer, LightTexture lightTexture,
                             Matrix4f projectionMatrix, CallbackInfo info) {
        this.elixirum$cachedCamera = camera;
    }

    @WrapOperation(method = "renderHitOutline", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"))
    private void renderHitOutline_modify(PoseStack stack, VertexConsumer consumer, VoxelShape shape,
                                         double x, double y, double z, float red, float green, float blue,
                                         float alpha, Operation<Void> original,
                                         @Local(argsOnly = true) Entity camera,
                                         @Local(argsOnly = true) BlockPos pos,
                                         @Local(argsOnly = true) BlockState state) {
        if (this.elixirum$cachedCamera == null && !state.is(ElixirumBlocks.GLASS_CAULDRON.get())) {
            original.call(stack, consumer, shape, x, y, z, red, green, blue, alpha);
        } else {
            GlassCauldronRenderer.translateOutline(stack, this.level, this.elixirum$cachedCamera, pos,
                    () -> original.call(stack, consumer, shape, x, y, z, red, green, blue, alpha));
        }
    }
}
