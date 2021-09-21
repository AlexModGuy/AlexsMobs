package com.github.alexthe666.alexsmobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelShieldOfTheDeep extends EntityModel<Entity> {
	private final ModelPart shield;
	private final ModelPart handle;

	public ModelShieldOfTheDeep() {
		texWidth = 64;
		texHeight = 64;

		shield = new ModelPart(this);
		shield.setPos(-2.0F, 16.0F, 0.0F);
		shield.texOffs(0, 0).addBox(-1.0F, -4.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);
		shield.texOffs(17, 15).addBox(-3.0F, -3.0F, -5.0F, 2.0F, 10.0F, 10.0F, 0.0F, false);
		shield.texOffs(27, 0).addBox(-4.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F, 0.0F, false);

		handle = new ModelPart(this);
		handle.setPos(8.0F, 8.0F, -8.0F);
		shield.addChild(handle);
		handle.texOffs(0, 25).addBox(-8.0F, -8.5F, 7.0F, 5.0F, 5.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		shield.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}