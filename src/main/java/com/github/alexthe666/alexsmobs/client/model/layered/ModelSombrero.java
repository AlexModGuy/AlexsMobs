package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSombrero extends HumanoidModel {
    public ModelPart sombrero;

    public ModelSombrero(ModelPart p_170677_) {
        super(p_170677_);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        head.addOrReplaceChild("sombrero", CubeListBuilder.create().texOffs(0, 64).addBox(-4.0F, -11.0F, -4.0F, 8.0F, 6.0F, 8.0F, deformation), PartPose.offset(0, 0, 0));
        head.addOrReplaceChild("sombrero2", CubeListBuilder.create().texOffs(22, 73).addBox(-11.0F, -8.0F, -11.0F, 22.0F, 3.0F, 22.0F, deformation), PartPose.offset(0, 0, 0));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static LayerDefinition createArmorLayerAprilFools(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        head.addOrReplaceChild("sombrero", CubeListBuilder.create().texOffs(0, 64).addBox(-4.0F, 7.0F, -4.0F, 8.0F, 6.0F, 8.0F, deformation), PartPose.offsetAndRotation(0, 0, 0, Mth.PI, 0, Mth.PI * 0.1F));
        head.addOrReplaceChild("sombrero2", CubeListBuilder.create().texOffs(22, 73).addBox(-11.0F, 10.0F, -11.0F, 22.0F, 3.0F, 22.0F, deformation), PartPose.offsetAndRotation(0, 0, 0, Mth.PI, 0, Mth.PI * 0.1F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
