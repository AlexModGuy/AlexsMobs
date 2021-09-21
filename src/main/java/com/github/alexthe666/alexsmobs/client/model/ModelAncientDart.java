package com.github.alexthe666.alexsmobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelAncientDart extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart main;
	private final ModelPart feathers;
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;

	public ModelAncientDart() {
		texWidth = 32;
		texHeight = 32;

		root = new ModelPart(this);
		root.setPos(0.0F, 0.0F, 0.0F);


		main = new ModelPart(this);
		main.setPos(0.0F, -1.0F, 0.0F);
		root.addChild(main);
		main.texOffs(11, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		main.texOffs(0, 0).addBox(-0.5F, -0.5F, -5.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		feathers = new ModelPart(this);
		feathers.setPos(0.0F, 1.0F, 1.0F);
		main.addChild(feathers);


		cube_r1 = new ModelPart(this);
		cube_r1.setPos(0.0F, -1.0F, 0.5F);
		feathers.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
		cube_r1.texOffs(0, 6).addBox(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 3.0F, 0.0F, false);

		cube_r2 = new ModelPart(this);
		cube_r2.setPos(0.0F, -1.0F, 0.5F);
		feathers.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -0.7854F);
		cube_r2.texOffs(7, 6).addBox(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}