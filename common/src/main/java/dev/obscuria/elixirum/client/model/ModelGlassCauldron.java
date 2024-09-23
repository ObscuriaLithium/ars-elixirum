package dev.obscuria.elixirum.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public final class ModelGlassCauldron extends Model {
    public final ModelPart root, main;

    public ModelGlassCauldron(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.main = root.getChild("main");
    }

    public static LayerDefinition createBodyLayer() {
        final var meshDefinition = new MeshDefinition();
        final var partDefinition = meshDefinition.getRoot();
        final var main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(0.0F, 23.3F, 0.0F));
        final var top = main.addOrReplaceChild("top", CubeListBuilder.create().texOffs(39, 23)
                        .addBox(-5.0F, -1.0F, -7.0F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 38)
                        .addBox(5.0F, -1.0F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -13.3F, 0.0F));
        top.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, -1.5708F, 1.1345F, -1.5708F));
        top.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.0F, 0.0F, 0.0F, -3.1416F, 0.0F, -2.7053F));
        top.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 1.5708F, -1.1345F, -1.5708F));
        top.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(7.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363F));
        top.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 38)
                        .addBox(-1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));
        top.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(39, 23)
                        .addBox(-5.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -3.1416F, 0.0F, 3.1416F));
        main.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8.0F, -5.75F, -8.0F, 16.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(0, 23)
                        .addBox(-6.5F, 1.25F, -6.5F, 13.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -6.55F, 0.0F));
        final var bottom = main.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(18, 38)
                        .addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.3F, 0.0F));
        final var bone = bottom.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, -0.6109F));
        bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public static LayerDefinition createFluidLayer() {
        final var meshDefinition = new MeshDefinition();
        final var partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("fluid", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.5F, -5.0F, -7.5F, 15.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)).texOffs(0, 21)
                        .addBox(-6.0F, 1.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -7F, 0.0F));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void translateFluid(PoseStack pose) {
        this.main.translateAndRotate(pose);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, int color) {
        this.main.render(pose, consumer, light, overlay, color);
    }
}
