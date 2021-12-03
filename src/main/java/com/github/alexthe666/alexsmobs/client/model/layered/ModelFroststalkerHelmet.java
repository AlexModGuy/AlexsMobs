package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ModelFroststalkerHelmet extends HumanoidModel {

    public ModelFroststalkerHelmet(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        head.addOrReplaceChild("frost", CubeListBuilder.create().texOffs(0, 17).addBox(-3.0F, -10.2F, -2.8F, 6.0F, 4.0F, 9.0F, deformation), PartPose.offset(0, 0, 0));
        head.addOrReplaceChild("horn", CubeListBuilder.create().texOffs(29, 29).addBox(-1.0F, -7.0F, 2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, -6.0F, -3.0F, 1.0472F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}