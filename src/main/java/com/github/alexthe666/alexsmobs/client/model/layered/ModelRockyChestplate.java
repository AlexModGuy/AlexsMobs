package com.github.alexthe666.alexsmobs.client.model.layered;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRockyChestplate extends HumanoidModel{
    private final ModelPart Body;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;

    public ModelRockyChestplate(ModelPart root) {
        super(root);
        this.Body = root.getChild("body").getChild("BodyRocky");
        this.LeftArm = root.getChild("left_arm").getChild("LeftArmRocky");
        this.RightArm = root.getChild("right_arm").getChild("RightArmRocky");
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition playerBody = partdefinition.getChild("body");
        PartDefinition playerLeftArm = partdefinition.getChild("left_arm");
        PartDefinition playerRightArm = partdefinition.getChild("right_arm");

        PartDefinition bodyRocky = playerBody.addOrReplaceChild("BodyRocky", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -0.5F, -2.0F, 10.0F, 13.0F, 9.0F, deformation)
                .texOffs(0, 23).addBox(-4.0F, 0.5F, 6.0F, 8.0F, 11.0F, 4.0F, deformation)
                .texOffs(25, 34).addBox(-2.0F, -0.5F, 6.0F, 4.0F, 13.0F, 4.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftArmRocky = playerLeftArm.addOrReplaceChild("LeftArmRocky", CubeListBuilder.create().texOffs(25, 23).addBox(-1.0F, -5F, -2.1F, 6.0F, 4.0F, 6.0F, deformation)
                .texOffs(0, 39).addBox(0.0F, -3.0F, -2.1F, 7.0F, 6.0F, 4.0F, deformation), PartPose.offset(-1.0F, 2.0F, 0.0F));

        PartDefinition rightArmRocky = playerRightArm.addOrReplaceChild("RightArmRocky", CubeListBuilder.create().texOffs(25, 23).mirror().addBox(-5.0F, -5F, -2.1F, 6.0F, 4.0F, 6.0F, deformation).mirror(false)
                .texOffs(0, 39).mirror().addBox(-7.0F, -3.0F, -2.1F, 7.0F, 6.0F, 4.0F, deformation).mirror(false), PartPose.offset(1.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}