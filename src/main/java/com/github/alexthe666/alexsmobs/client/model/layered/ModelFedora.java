package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ModelFedora extends HumanoidModel {

    public ModelFedora(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        head.addOrReplaceChild("fedora", CubeListBuilder.create().texOffs(0, 44).addBox(-3.0F, -3.55F, -3.0F, 6.0F, 4.0F, 6.0F, deformation), PartPose.offset(0, -8, 0));
        head.addOrReplaceChild("fedora_shade", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 10.0F, deformation), PartPose.offset(0, -8.05F, 0));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

   /* public ModelFedora(float modelSize) {
        super(modelSize, 0, 64, 64);
        texWidth = 64;
        texHeight = 64;
        fedora = new ModelPart(this);
        fedora.setPos(0.0F, 8F, 0.0F);
        fedora.setTextureOffset(0, 44).addBox(-3.0F, -3.55F, -3.0F, 6.0F, 4.0F, 6.0F, modelSize, false);
        fedora_shade = new ModelPart(this);
        fedora_shade.setPos(0.0F, -0.05F, 0.0F);
        fedora_shade.setTextureOffset(0, 32).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 10.0F, modelSize, false);
        head.addChild(fedora);
        fedora.addChild(fedora_shade);
    }*/
}