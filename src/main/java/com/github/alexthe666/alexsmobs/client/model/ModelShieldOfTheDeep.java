package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelShieldOfTheDeep extends AdvancedEntityModel<Entity> {
	private final AdvancedModelBox shield;
	private final AdvancedModelBox handle;

	public ModelShieldOfTheDeep() {
		texWidth = 64;
		texHeight = 64;

		shield = new AdvancedModelBox(this);
		shield.setPos(-2.0F, 16.0F, 0.0F);
		shield.setTextureOffset(0, 0).addBox(-1.0F, -4.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);
		shield.setTextureOffset(17, 15).addBox(-3.0F, -3.0F, -5.0F, 2.0F, 10.0F, 10.0F, 0.0F, false);
		shield.setTextureOffset(27, 0).addBox(-4.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F, 0.0F, false);

		handle = new AdvancedModelBox(this);
		handle.setPos(8.0F, 8.0F, -8.0F);
		shield.addChild(handle);
		handle.setTextureOffset(0, 25).addBox(-8.0F, -8.5F, 7.0F, 5.0F, 5.0F, 2.0F, 0.0F, false);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(handle, shield);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		shield.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(shield);
	}
}