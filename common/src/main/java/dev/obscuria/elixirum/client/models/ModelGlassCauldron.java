package dev.obscuria.elixirum.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;

public final class ModelGlassCauldron extends Model {

    public final ModelPart main;

    public ModelGlassCauldron(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.main = root.getChild("main");
    }

    public void translateFluid(PoseStack pose) {
        main.translateAndRotate(pose);
    }

    public static LayerDefinition createMainLayer() {
        final var meshDefinition = new MeshDefinition();
        final var partDefinition = meshDefinition.getRoot();

        final var main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(0.0f, 23.3f, 0.0f));
        final var top = main.addOrReplaceChild("top", CubeListBuilder.create()
                        .texOffs(39, 23).addBox(-5.0f, -1.0f, -7.0f, 10.0f, 2.0f, 2.0f, new CubeDeformation(0.0f))
                        .texOffs(0, 38).addBox(5.0f, -1.0f, -7.0f, 2.0f, 2.0f, 14.0f, new CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -13.3f, 0.0f));
        top.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-1.0f, -2.5f, -2.0f, 2.0f, 5.0f, 4.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -7.0f, -1.5708f, 1.1345f, -1.5708f));
        top.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-1.0f, -2.5f, -2.0f, 2.0f, 5.0f, 4.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, 0.0f, 0.0f, -3.1416f, 0.0f, -2.7053f));
        top.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-1.0f, -2.5f, -2.0f, 2.0f, 5.0f, 4.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 7.0f, 1.5708f, -1.1345f, -1.5708f));
        top.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-1.0f, -2.5f, -2.0f, 2.0f, 5.0f, 4.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.4363f));
        top.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                        .texOffs(0, 38).addBox(-1.0f, -1.0f, -7.0f, 2.0f, 2.0f, 14.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, 0.0f, 0.0f, -3.1416f, 0.0f, 3.1416f));
        top.addOrReplaceChild("cube_r6", CubeListBuilder.create()
                        .texOffs(39, 23).addBox(-5.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, -3.1416f, 0.0f, 3.1416f));
        main.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8.0f, -5.75f, -8.0f, 16.0f, 7.0f, 16.0f, new CubeDeformation(0.0f))
                        .texOffs(0, 23).addBox(-6.5f, 1.25f, -6.5f, 13.0f, 2.0f, 13.0f, new CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -6.55f, 0.0f));
        final var bottom = main.addOrReplaceChild("bottom", CubeListBuilder.create()
                        .texOffs(18, 38).addBox(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, new CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.3f, 0.0f));
        final var bone = bottom.addOrReplaceChild("bone", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -0.1f, 0.0f, 0.0f, 0.0f, -0.6109f));
        bone.addOrReplaceChild("cube_r7", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.7854f, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public static LayerDefinition createFluidLayer() {
        final var meshDefinition = new MeshDefinition();
        final var partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("main", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-7.5f, -5.0f, -7.5f, 15.0f, 6.0f, 15.0f, new CubeDeformation(0.0f))
                        .texOffs(0, 21).addBox(-6.0f, 1.0f, -6.0f, 12.0f, 2.0f, 12.0f, new CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        main.render(pose, consumer, light, overlay, r, g, b, a);
    }
}
