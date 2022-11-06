package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ModelSpikedTurtleShell extends HumanoidModel{

	public ModelSpikedTurtleShell(ModelPart p_170677_) {
		super(p_170677_);
	}

	public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition head = partdefinition.getChild("head");

		head.addOrReplaceChild("spikes1", CubeListBuilder.create().texOffs(34, 15).addBox(0.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, deformation), PartPose.offset(0, 24.0F, 0));
		head.addOrReplaceChild("spikes2", CubeListBuilder.create().texOffs(34, 15).addBox(-4.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, deformation), PartPose.offset(0, 24.0F, 0));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}
}