package dev.obscuria.elixirum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private MixinHumanoidArmorLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void renderArmorPiece_Modify(PoseStack pose, MultiBufferSource source, T entity,
                                         EquipmentSlot slot, int light, A model, CallbackInfo info) {

        if (entity.getItemBySlot(slot).is(ElixirumItems.ALCHEMIST_EYE.asItem())) info.cancel();
    }
}
