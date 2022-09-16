package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.geom.PartPose;

public class ModelUnsettlingKimono extends HumanoidModel {
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;

    public ModelUnsettlingKimono(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition playerBody = partdefinition.getChild("body");
        PartDefinition playerLeftArm = partdefinition.getChild("left_arm");
        PartDefinition playerRightArm = partdefinition.getChild("right_arm");

        PartDefinition body = playerBody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 17.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = playerLeftArm.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(21, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.6F)), PartPose.offset(-0.5F, 0.0F, 0.0F));
        PartDefinition right_arm = playerRightArm.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(21, 18).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.6F)).mirror(false), PartPose.offset(0.5F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}