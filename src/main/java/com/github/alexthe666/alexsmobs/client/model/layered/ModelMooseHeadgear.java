package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelMooseHeadgear extends HumanoidModel {

    public ModelMooseHeadgear(ModelPart p_170677_) {
        super(p_170677_);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        head.addOrReplaceChild("hornL", CubeListBuilder.create().texOffs(3, 17).addBox(0.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, deformation), PartPose.offset(5.0F, -8.0F, 1.0F));
        head.addOrReplaceChild("hornR", CubeListBuilder.create().texOffs(3, 17).mirror().addBox(-10.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, deformation), PartPose.offset(-5.0F, -8.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
