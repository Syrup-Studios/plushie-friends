package net.syrupstudios.plushiefriends.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PlushieModel {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart classicRightArm;
    private final ModelPart classicLeftArm;
    private final ModelPart slimRightArm;
    private final ModelPart slimLeftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public PlushieModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.classicRightArm = root.getChild("classic_right_arm");
        this.classicLeftArm = root.getChild("classic_left_arm");
        this.slimRightArm = root.getChild("slim_right_arm");
        this.slimLeftArm = root.getChild("slim_left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F)
                        .texOffs(16, 32).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)),
                PartPose.offset(0.0F, -12.0F, 0.0F));

        partDefinition.addOrReplaceChild("classic_right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(40, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-5.0F, -11.0F, 0.0F, 0.0F, 0.0F, 0.25F));

        partDefinition.addOrReplaceChild("classic_left_arm",
                CubeListBuilder.create()
                        .texOffs(32, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(48, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(5.0F, -11.0F, 0.0F, 0.0F, 0.0F, -0.25F));

        partDefinition.addOrReplaceChild("slim_right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F)
                        .texOffs(40, 32).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-4.5F, -11.0F, 0.0F, 0.0F, 0.0F, 0.25F));

        partDefinition.addOrReplaceChild("slim_left_arm",
                CubeListBuilder.create()
                        .texOffs(32, 48).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F)
                        .texOffs(48, 48).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(4.5F, -11.0F, 0.0F, 0.0F, 0.0F, -0.25F));

        partDefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-2.0F, -2.0F, -0.5F, -1.48F, 0.15F, 0.0F));

        partDefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(2.0F, -2.0F, -0.5F, -1.48F, -0.15F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, boolean isSlim) {
        //? if >=1.21 {
        /*this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        if (isSlim) {
            this.slimRightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
            this.slimLeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        } else {
            this.classicRightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
            this.classicLeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
        *///?} else {
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (isSlim) {
            this.slimRightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            this.slimLeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.classicRightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            this.classicLeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        //?}
    }
}
